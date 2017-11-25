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

import net.digger.util.crc.config.Config;

/**
 * Interface for checksum and CRC calculations.
 * 
 * @author walton
 *
 * @param <T> Class of Config instance used by Computer implementation.
 * @param <U> Class of Computer implementation used by Config instance.
 */
public interface Computer<T extends Config<U, T>, U extends Computer<T, U>> {
	// ##### Slower (computational) CRC calculations

	/**
	 * Compute the CRC of a given message using the slower computational method.
	 * 
	 * @param config CRC configuration to use.
	 * @param message Message to calculate CRC for.
	 * @return The CRC of the message.
	 */
	public long slow(T config, byte[] message);

	/**
	 * Update an incremental CRC with a new byte using the slower computational method.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Current value of the CRC.  Use {@code null} to start.
	 * @param b Next byte of the message to calculate CRC for.
	 * @return The CRC of the message so far.
	 */
	public long slowUpdate(T config, Long crc, byte b);

	// ##### CRC table initialization calculations

	/**
	 * Populate the partial CRC lookup table.
	 * 
	 * @param config CRC configuration to use.
	 */
	public void fastInit(T config);

	// ##### Faster (table-driven) CRC calculations

	/**
	 * Compute the CRC of a given message using the faster table-driven method.
	 * 
	 * @param config CRC configuration to use.
	 * @param message Message to calculate CRC for.
	 * @return The CRC of the message.
	 */
	public long fast(T config, byte[] message);
	
	/**
	 * Update an incremental CRC with a new byte using the faster table-driven method.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Current value of the CRC.  Use {@code null} to start.
	 * @param b Next byte of the message to calculate CRC for.
	 * @return The CRC of the message so far.
	 */
	public long fastUpdate(T config, Long crc, byte b);
}
