package de.beckers.members.push;

import com.google.api.client.util.Key;

import lombok.Data;
import lombok.experimental.Builder;

@Data
@Builder
public class GcmRequest {
	@Key
	private String to;
	
	@Key
	private GcmData data;
	
	@Key
	private int time_to_live;
}
