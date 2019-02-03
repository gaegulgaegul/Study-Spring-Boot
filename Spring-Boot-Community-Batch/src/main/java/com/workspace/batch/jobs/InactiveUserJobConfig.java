package com.workspace.batch.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.workspace.batch.domain.User;
import com.workspace.batch.domain.enums.UserStatus;
import com.workspace.batch.jobs.readers.QueueItemReader;
import com.workspace.batch.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
public class InactiveUserJobConfig {
	
	private UserRepository userRepository;
	
	@Bean
	public Job inactionUserJob(JobBuilderFactory jobBuilderFactory, Step inactiveJobStep) {
		return jobBuilderFactory.get("inactiveUserJob")
				.preventRestart()
				.start(inactiveJobStep)
				.build();
	}
	
	@Bean
	public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("inactiveUserStep")
				.<User, User> chunk(10)
				.reader(inactiveUserReader())
				.processor(inactiveUserProcessor())
				.writer(inactiveUserWriter())
				.build();
	}
	
	@Bean
	@StepScope
	public QueueItemReader<User> inactiveUserReader() {
		List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(
				LocalDateTime.now().minusYears(1), UserStatus.ACTIVE, null);
		return new QueueItemReader<>(oldUsers);
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
	
	public ItemWriter<User> inactiveUserWriter() {
		return ((List<? extends User> users) -> userRepository.saveAll(users));
	}

}
