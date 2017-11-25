/**
 * Copyright © 2017  David Walton
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.digger.util.crc.computer;

import net.digger.util.crc.config.ChecksumConfig;

/**
 * Checksum computation class.
 * 
 * @author walton
 */
public class ChecksumComputer implements Computer<ChecksumConfig, ChecksumComputer> {
	/**
	 * Compute the checksum of a given message.
	 * <p>
	 * For ChecksumComputer, there is actually no difference between fast and slow methods.
	 * 
	 * @param config Checksum configuration to use.
	 * @param message Message to calculate checksum for.
	 * @return The checksum of the message.
	 */
	@Override
	public long slow(ChecksumConfig config, byte[] message) {
		return fast(config, message);
	}

	/**
	 * Update an incremental checksum with a new byte.
	 * <p>
	 * For ChecksumComputer, there is actually no difference between fast and slow methods.
	 * 
	 * @param config Checksum configuration to use.
	 * @param sum Current value of the checksum.  Use {@code null} to start.
	 * @param b Next byte of the message to calculate checksum for.
	 * @return The checksum of the message so far.
	 */
	@Override
	public long slowUpdate(ChecksumConfig config, Long sum, byte b) {
		return fastUpdate(config, sum, b);
	}

	/**
	 * Not implemented in ChecksumComputer.
	 * 
	 * @param config Checksum configuration to use.
	 */
	@Override
	public void fastInit(ChecksumConfig config) {}

	/**
	 * Compute the checksum of a given message.
	 * <p>
	 * For ChecksumComputer, there is actually no difference between fast and slow methods.
	 * 
	 * @param config Checksum configuration to use.
	 * @param message Message to calculate checksum for.
	 * @return The checksum of the message.
	 */
	@Override
	public long fast(ChecksumConfig config, byte[] message) {
		long sum = config.initialValue;
		for (byte b : message) {
			sum += b;
		}
		return sum & config.mask;
	}

	/**
	 * Update an incremental checksum with a new byte.
	 * <p>
	 * For ChecksumComputer, there is actually no difference between fast and slow methods.
	 * 
	 * @param config Checksum configuration to use.
	 * @param sum Current value of the checksum.  Use {@code null} to start.
	 * @param b Next byte of the message to calculate checksum for.
	 * @return The checksum of the message so far.
	 */
	@Override
	public long fastUpdate(ChecksumConfig config, Long sum, byte b) {
		if (sum == null) {
			sum = config.initialValue;
		}
		sum += b;
		return sum & config.mask;
	}
}
