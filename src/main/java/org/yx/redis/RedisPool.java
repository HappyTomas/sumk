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
package org.yx.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.yx.log.ConsoleLog;
import org.yx.log.Log;
import org.yx.main.SumkServer;

public class RedisPool {
	private static final Map<String, Redis> map = new ConcurrentHashMap<>();

	static Redis _defaultRedis;
	static {
		if (!SumkServer.isDestoryed()) {
			try {
				ConsoleLog.get("sumk.SYS").debug("redis pool init");
				RedisLoader.init();
			} catch (Exception e) {
				Log.printStack(e);
				System.exit(-1);
			}
		}
	}

	public static Redis get(String alias) {
		if (alias == null) {
			return _defaultRedis;
		}
		alias = alias.toLowerCase();
		Redis r = map.get(alias);
		if (r != null) {
			return r;
		}
		return _defaultRedis;
	}

	public static Redis getRedisExactly(String alias) {
		alias = alias.toLowerCase();
		return map.get(alias);
	}

	public static Redis defaultRedis() {
		return _defaultRedis;
	}

	public static void put(String alias, Redis redis) {
		Redis old = map.put(alias.toLowerCase(), redis);
		Log.get("sumk.redis").info("redis name {} : {}", alias, redis);
		if (old != null) {
			old.shutdown();
			Log.get("sumk.redis").info("shutdown old redis {} : {}", alias, redis);
		}
	}

	public static void shutdown() {
		Set<Redis> redises = new HashSet<>(map.values());
		if (_defaultRedis != null) {
			redises.add(_defaultRedis);
		}

		for (Redis r : redises) {
			r.shutdown();
		}
		map.clear();
	}
}
