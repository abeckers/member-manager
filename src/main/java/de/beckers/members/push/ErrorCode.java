package de.beckers.members.push;

import com.google.api.client.util.NullValue;
import com.google.api.client.util.Value;

public enum ErrorCode {
	@Value("NotRegistered") NotRegistered,

	@Value("MissingRegistration") MissingRegistration,

	@Value("InvalidRegistration") InvalidRegistration,

	@Value("InvalidPackageName") InvalidPackageName,

	@Value("MismatchSenderId") MismatchSenderId,

	@Value("MessageTooBig") MessageTooBig,

	@Value("InvalidDataKey") InvalidDataKey,

	@Value("InvalidTtl") InvalidTtl,

	@Value("Unavailable") Unavailable,

	@Value("InternalServerError") InternalServerError,

	@Value("DeviceMessageRateExceeded") DeviceMessageRateExceeded,

	@Value("TopicsMessageRateExceeded") TopicsMessageRateExceeded,
	
	@NullValue OK
}
