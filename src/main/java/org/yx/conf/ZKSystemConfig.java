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
package org.yx.conf;

public class ZKSystemConfig extends AbstractZKConfig {
	private String dataPath = System.getProperty("sumk.zkconf.root", "/sumk/appinfo");
	private String zkUrl = System.getProperty("sumk.zkconf.url");

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public void setZkUrl(String zkUrl) {
		this.zkUrl = zkUrl;
	}

	public String getDataPath() {
		return dataPath;
	}

	public String getZkUrl() {
		return zkUrl;
	}

}
