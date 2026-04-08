package util.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorModel {

	private String message;
	private String recommendation;
	private HttpStatus code;

	public ErrorModel(String message, String recommendation, HttpStatus code) {
		super();
		this.message = message;
		this.recommendation = recommendation;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public HttpStatus getCode() {
		return code;
	}

	public void setCode(HttpStatus code) {
		this.code = code;
	}

}
