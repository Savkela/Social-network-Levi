package com.levi9.socialnetwork.Exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ErrorResponse {
		// customizing timestamp serialization format
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
		private LocalDateTime timestamp;
		private int code;
		private String status;
		private String message;
		private String stackTrace;

		public ErrorResponse(HttpStatus httpStatus, String message, String stackTrace) {
			this.timestamp = LocalDateTime.now();
			this.code = httpStatus.value();
			this.status = httpStatus.name();
			this.message = message;
			this.stackTrace = stackTrace;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getStackTrace() {
			return stackTrace;
		}

		public void setStackTrace(String stackTrace) {
			this.stackTrace = stackTrace;
		}
		
	}