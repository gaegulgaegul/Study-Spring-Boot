package com.workspace.batch.jobs.inactive;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.workspace.batch.domain.User;
import com.workspace.batch.domain.enums.Grade;
import com.workspace.batch.domain.enums.UserStatus;
import com.workspace.batch.jobs.inactive.listener.InactiveIJobListener;
import com.workspace.batch.jobs.inactive.listener.InactiveJobExecutionDecider;
import com.workspace.batch.jobs.inactive.listener.InactiveStepListener;
import com.workspace.batch.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Configuration
public class InactiveUserJobConfig {
	
	private final static int CHUNK_SIZE = 15;
	private final EntityManagerFactory entityManagerFactory;
	
	private UserRepository userRepository;
	
//	@Bean
//	public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, Step inactiveJobStep) {
//		return jobBuilderFactory.get("inactiveUserJob")
//				.preventRestart()
//				.start(inactiveJobStep)
//				.build();
//	}
	
//	@Bean
//	public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, InactiveIJobListener inactiveIJobListener, Step inactiveJobStep) {
//		return jobBuilderFactory.get("inactiveUserJob")
//				.preventRestart()
//				.listener(inactiveIJobListener)
//				.start(inactiveJobStep)
//				.build();
//	}
	
//	@Bean
//	public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, InactiveIJobListener inactiveIJobListener, Flow inactiveJobFlow) {
//		return jobBuilderFactory.get("inactiveUserJob")
//				.preventRestart()
//				.listener(inactiveIJobListener)
//				.start(inactiveJobFlow)
//				.end()
//				.build();
//	}
	
//	@Bean
//	public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory,
//								InactiveIJobListener inactiveIJobListener,
//								Flow multiFlow) {
//		return jobBuilderFactory.get("inactiveUserJob")
//				.preventRestart()
//				.listener(inactiveIJobListener)
//				.start(multiFlow)
//				.end()
//				.build();
//	}
//	
//	@Bean
//	public Flow multiFlow(Step inactiveJobStep) {
//		Flow flows[] = new Flow[5];
//		IntStream.range(0, flows.length).forEach(i -> flows[i] = new FlowBuilder<Flow>("MultiFlow"+i)
//																	.from(inactiveJobFlow(inactiveJobStep)).end()
//		);
//		
//		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("MultiFlowTest");
//		return flowBuilder
//				.split(taskExecutor())
//				.add(flows)
//				.build();
//	}
	
	@Bean
	public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory,
								InactiveIJobListener inactiveIJobListener,
								Step partitionerStep) {
		return jobBuilderFactory.get("inactiveUserJob")
				.preventRestart()
				.listener(inactiveIJobListener)
				.start(partitionerStep)
				.build();
	}
	
//	@Bean
//	public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory, JpaPagingItemReader<User> inactiveUserJpaReader) {
//		return stepBuilderFactory.get("inactiveUserStep")
//				.<User, User> chunk(CHUNK_SIZE)
//				.reader(inactiveUserJpaReader())
//				.processor(inactiveUserProcessor())
//				.writer(inactiveUserWriter())
//				.build();
//	}
	
//	@Bean
//	public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory, ListItemReader<User> inactiveUserReader) {
//		return stepBuilderFactory.get("inactiveUserStep")
//				.<User, User> chunk(CHUNK_SIZE)
//				.reader(inactiveUserReader)
//				.processor(inactiveUserProcessor())
//				.writer(inactiveUserWriter())
//				.build();
//	}
	
//	@Bean
//	public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory,
//			ListItemReader<User> inactiveUserReader,
//			InactiveStepListener inactiveStepListener) {
//		return stepBuilderFactory.get("inactiveUserStep")
//				.<User, User> chunk(CHUNK_SIZE)
//				.reader(inactiveUserReader)
//				.processor(inactiveUserProcessor())
//				.writer(inactiveUserWriter())
//				.listener(inactiveStepListener)
//				.build();
//	}
	
	@Bean
	public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory,
			ListItemReader<User> inactiveUserReader,
			InactiveStepListener inactiveStepListener,
			TaskExecutor taskExecutor) {
		return stepBuilderFactory.get("inactiveUserStep")
				.<User, User> chunk(CHUNK_SIZE)
				.reader(inactiveUserReader)
				.processor(inactiveUserProcessor())
				.writer(inactiveUserWriter())
				.listener(inactiveStepListener)
				.taskExecutor(taskExecutor)
				.throttleLimit(2)
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("Batch_Task");
	}
	
	@Bean
	@JobScope
	public Step partitionerStep(StepBuilderFactory stepBuilderFactory, Step inactiveJobStep) {
		return stepBuilderFactory
				.get("partitionerStep")
				.partitioner("partitionerStep", new InactiveUserPartitioner())
				.gridSize(5)
				.step(inactiveJobStep)
				.taskExecutor(taskExecutor())
				.build();
	}
	
//	@Bean
//	public Flow inactiveJobFlow(Step inactiveJobStep) {
//		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("inactiveJobFlow");
//		return flowBuilder
//				.start(new InactiveJobExecutionDecider())
//				.on(FlowExecutionStatus.FAILED.getName()).end()
//				.on(FlowExecutionStatus.COMPLETED.getName()).to(inactiveJobStep)
//				.end();
//	}
	

	@Bean
	private Flow inactiveJobFlow(Step inactiveJobStep) {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("inactiveJobFlow");
		return flowBuilder
				.start(new InactiveJobExecutionDecider())
				.on(FlowExecutionStatus.FAILED.getName()).end()
				.on(FlowExecutionStatus.COMPLETED.getName()).to(inactiveJobStep)
				.end();
	}
	
//	1. Queue 방식의 ItemReader
//	@Bean
//	@StepScope
//	public QueueItemReader<User> inactiveUserReader() {
//		List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(
//				LocalDateTime.now().minusYears(1), UserStatus.ACTIVE);
//		return new QueueItemReader<>(oldUsers);
//	}
	
//	2. List 방식의 ItemReader
//	@Bean
//	@StepScope
//	public ListItemReader<User> inactiveUserReader() {
//		List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(
//				LocalDateTime.now().minusYears(1), UserStatus.ACTIVE);
//		return new ListItemReader<>(oldUsers);
//	}

//	3. JpaPagingItemReader
	@Bean(destroyMethod="")
	@StepScope
	public JpaPagingItemReader<User> inactiveUserJpaReader() {
		JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReader<>();
		jpaPagingItemReader.setQueryString("select u from user as u where u.updatedDate < :updatedDate and u.status = :status");

		Map<String, Object> map = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();
		map.put("updatedDate", now.minusYears(1));
		map.put("status", UserStatus.ACTIVE);
		
		jpaPagingItemReader.setParameterValues(map);
		jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
		jpaPagingItemReader.setPageSize(CHUNK_SIZE);
		return jpaPagingItemReader;
	}
	
//	JpaPagingItemReader가 항상 인덱스 0을 반환하도록 수
//	@Bean(destroyMethod="")
//	@StepScope
//	public JpaPagingItemReader<User> inactiveUserJpaReader() {
//		JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReader() {
//			
//			@Override
//			public int getPage() {
//				return 0;
//			}
//			
//		};
//	}
	
//	@Bean
//	@StepScope
//	public ListItemReader<User> inactiveUserReader(@Value("#{jobParameters[nowDate]}") Date nowDate, UserRepository userRepository) {
//		LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());
//		List<User> inactiveUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(now.minusYears(1), UserStatus.ACTIVE);
//		return new ListItemReader<>(inactiveUsers);
//	}
	
	@Bean
	@StepScope
	public ListItemReader<User> inactiveUserReader(@Value("#{stepExecutionContext[grade]}") String grade, UserRepository userRepository) {
		log.info(Thread.currentThread().getName());
		List<User> inactiveUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1),
																							UserStatus.ACTIVE,
																							Grade.valueOf(grade));
		return new ListItemReader<>(inactiveUsers);
	}
	
	public ItemProcessor<User, User> inactiveUserProcessor() {
//		return User::setInactive;
		return new ItemProcessor<User, User>() {
			
			@Override
			public User process(User user) throws Exception {
				return user.setInactive();
			}
			
		};
	}
	
//	public ItemWriter<User> inactiveUserWriter() {
//		return ((List<? extends User> users) -> userRepository.saveAll(users));
//	}
	
	private JpaItemWriter<User> inactiveUserWriter() {
		JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}

}
