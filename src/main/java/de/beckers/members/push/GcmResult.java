package de.beckers.members.push;

import com.google.api.client.util.Key;

import lombok.Data;

@Data
public class GcmResult {
	@Key
	private String message_id;

	@Key
	private String registration_id;

	@Key
	private ErrorCode error;
}
