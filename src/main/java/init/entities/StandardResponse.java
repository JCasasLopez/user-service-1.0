package init.entities;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public class StandardResponse {
	
	private LocalDateTime timestamp;
	private String mensaje;
	private String detalles;
	private HttpStatus status;
	
	public StandardResponse(LocalDateTime timestamp, String mensaje, String detalles, HttpStatus status) {
		this.timestamp = timestamp;
		this.mensaje = mensaje;
		this.detalles = detalles;
		this.status = status;
	}

	public StandardResponse() {
		super();
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getDetalles() {
		return detalles;
	}

	public void setDetalles(String detalles) {
		this.detalles = detalles;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
}
