/**
 * Copyright (C) 2016 - 2030 youtongluan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yx.exception;

import org.yx.rpc.Host;

public class ConnectionException extends CodeException {

	private static final long serialVersionUID = -6041686677407485992L;

	private Host host;

	public ConnectionException(int code, String msg, Host addr) {
		super(code, addr + "->" + msg);
		this.host = addr;
	}

	public Host getHost() {
		return host;
	}

}
