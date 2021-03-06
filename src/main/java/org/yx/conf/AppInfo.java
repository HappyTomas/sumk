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

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import org.yx.bean.Loader;
import org.yx.log.Log;
import org.yx.rpc.LocalhostUtil;
import org.yx.util.StringUtil;

public final class AppInfo {
	public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	private static final List<Observer> observers = new ArrayList<>(4);
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	private static SystemConfig info;
	private static String pid;
	private static String localIp;

	static {
		try {
			String temp = ManagementFactory.getRuntimeMXBean().getName();
			pid = temp.substring(0, temp.indexOf("@"));
		} catch (Exception e) {
			e.printStackTrace();
			pid = "UNKNOW";
		}
		init();
	}

	private static void init() {
		try {
			if (refreshConfig()) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {

			String clzName = System.getProperty("sumk.appinfo.class");
			if (clzName != null && clzName.length() > 5) {
				Class<?> clz = Loader.loadClass(clzName);
				Object obj = clz.newInstance();
				if (SystemConfig.class.isInstance(obj)) {
					System.out.println("use " + clzName + " for appInfo");
					info = (SystemConfig) obj;
					info.initAppInfo();
				}
			}
		} catch (Throwable e) {
			System.out.println("#AppInfo#error for sumk.appinfo.class:");
			e.printStackTrace();
		}
		try {
			if (info == null) {
				info = new AppPropertiesInfo();
			}
			info.initAppInfo();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static String pid() {
		return pid;
	}

	static boolean refreshConfig() {
		SystemConfig config = SystemConfigHolder.config;
		if (info == config || config == null) {
			return false;
		}
		info = config;
		info.initAppInfo();
		notifyUpdate();
		return true;
	}

	public static synchronized void addObserver(Observer ob) {
		if (observers.contains(ob)) {
			return;
		}
		observers.add(ob);
		ob.update(null, null);
	}

	public static String getServerZKUrl() {
		String url = info.get("soa.zk.server");
		if (url != null && url.length() > 0) {
			return url;
		}
		return info.get("sumk.zkurl");
	}

	public static String getClinetZKUrl() {
		String url = info.get("soa.zk.client");
		if (url != null && url.length() > 0) {
			return url;
		}
		return info.get("sumk.zkurl");
	}

	public static String getIp() {
		String ip = localIp;
		if (ip != null) {
			return ip;
		}
		try {
			return LocalhostUtil.getLocalIP();
		} catch (Exception e) {
			Log.printStack(e);
		}
		return "0.0.0.0";
	}

	/**
	 * @param defaultValue
	 *            如果没有设置的话，就返回这个默认值
	 * @return 当前应用的id
	 */
	public static String appId(String defaultValue) {
		return get("sumk.appId", defaultValue);
	}

	/**
	 * @param defaultValue
	 *            如果没有设置的话，就返回这个默认值
	 * @return 大系统的id
	 */
	public static String groupId(String defaultValue) {
		return get("sumk.groupId", defaultValue);
	}

	/**
	 * 
	 * 
	 * @param name
	 *            name
	 * @return name不存在的话，返回null
	 */
	public static String get(String name) {
		return info.get(name);
	}

	public static String getLatin(String name) {
		String v = info.get(name);
		return StringUtil.toLatin(v);
	}

	public static String getLatin(String name, String defaultValue) {
		String v = getLatin(name);
		return StringUtil.isEmpty(v) ? defaultValue : v;
	}

	public static String get(String name, String defaultValue) {
		String v = info.get(name);
		if (v == null || v.isEmpty()) {
			return defaultValue;
		}
		return v;
	}

	public static String get(String name1, String name2, String defaultValue) {
		String value = info.get(name1);
		if (value != null && value.length() > 0) {
			return value;
		}
		return get(name2, defaultValue);
	}

	public static int getInt(String name, int defaultValue) {
		String value = info.get(name);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long getLong(String name, long defaultValue) {
		String value = info.get(name);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Charset systemCharset() {

		String charsetName = info == null ? null : info.get("sumk.charset");
		if (StringUtil.isEmpty(charsetName) || charsetName.equalsIgnoreCase(DEFAULT_CHARSET.name())) {
			return DEFAULT_CHARSET;
		}
		if (!Charset.isSupported(charsetName)) {
			Log.get("sumk.SYS").error("charset '{}' is not supported", charsetName);
			return DEFAULT_CHARSET;
		}
		return Charset.forName(charsetName);
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		String value = info.get(name);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		value = value.toLowerCase();
		return "1".equals(value) || "true".equals(value);
	}

	public static Map<String, String> subMap(String prefix) {
		int len = prefix.length();
		Map<String, String> map = new HashMap<>();
		for (String name : info.keys()) {
			if (name.startsWith(prefix)) {
				map.put(name.substring(len), info.get(name));
			}
		}
		return map;
	}

	public static void notifyUpdate() {
		localIp = info.get("sumk.ip");

		AppInfo.observers.forEach(ob -> {
			ob.update(null, info);
		});
	}

}
