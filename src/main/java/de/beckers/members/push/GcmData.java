package de.beckers.members.push;

import com.google.api.client.util.Key;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class GcmData {
	@Key
	private GcmMessage message;
}
