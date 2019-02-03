package com.workspace.batch.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.workspace.batch.domain.enums.Grade;
import com.workspace.batch.domain.enums.SocialType;
import com.workspace.batch.domain.enums.UserStatus;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = {"idx", "email"})
@NoArgsConstructor
@Entity
@Table
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;
	
	@Column
	private String name;
	
	@Column
	private String password;
	
	@Column
	private String email;
	
	@Column
	private String principal;
	
	@Column
	@Enumerated(EnumType.STRING)
	private SocialType socialType;
	
	@Column
	@Enumerated(EnumType.STRING)
	private UserStatus status;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Grade grade;
	
	@Column
	private LocalDateTime CreatedDate;
	
	@Column
	private LocalDateTime updatedDate;

	@Builder
	public User(String name, String password, String email, String principal, SocialType socialType, UserStatus status,
			Grade grade, LocalDateTime createdDate, LocalDateTime updatedDate) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.principal = principal;
		this.socialType = socialType;
		this.status = status;
		this.grade = grade;
		CreatedDate = createdDate;
		this.updatedDate = updatedDate;
	}

	public User setInactive() {
		status = UserStatus.INACTIVE;
		return this;
	}
	
}
