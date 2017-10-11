package de.beckers.members.push;

import com.google.api.client.util.Key;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GcmMessage {
	@Key
	private String message;
	
    @Key
	private String title;
    
    @Key
    private String score;
    
    @Key
    private String team;
}
