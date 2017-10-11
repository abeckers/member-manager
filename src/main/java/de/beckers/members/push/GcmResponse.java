package de.beckers.members.push;

import java.util.List;

import com.google.api.client.util.Key;

import lombok.Data;

@Data
public class GcmResponse {
	@Key
	private Long message_id;

	@Key
	private Long multicast_id;
	
	@Key
	private Long success;
	
	@Key
	private Long failure;
	
	@Key
	private Long canonical_ids;
	
	@Key
	private List<GcmResult> results;
}
