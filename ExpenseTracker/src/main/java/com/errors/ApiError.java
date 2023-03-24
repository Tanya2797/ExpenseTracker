package com.errors;

import java.util.Date;
import java.util.List;


public class ApiError {

        private Date timestamp;
	    private String message;
	    private String path;
	    
		public ApiError(Date timestamp, String message, String path) {
			super();
			this.timestamp = timestamp;
			this.message = message;
			this.path = path;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
		

	

}
