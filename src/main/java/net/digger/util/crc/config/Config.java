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
package net.digger.util.crc.config;

import net.digger.util.crc.computer.Computer;

/**
 * Base class for checksum and CRC configurations.
 */
public abstract class Config<U extends Computer<T, U>, T extends Config<U, T>> {
	/**
	 * Class of Computer to use.
	 */
	public final Class<U> computer;
	/**
	 * Name of this configuration.
	 */
	public final String name;
	/**
	 * Number of bits in final output.
	 */
	public final int bits;
	/**
	 * Number of bytes in final output.
	 */
	public final int bytes;
	/**
	 * Final output bitmask.
	 */
	public final long mask;
	/**
	 * Initial value for this configuration.
	 */
	public final long initialValue;

	/**
	 * Create a new configuration.
	 * 
	 * @param computer Class of Computer to use.
	 * @param name Name of this configuration.
	 * @param bits Number of bits in final output.
	 * @param initialValue Initial value for this configuration.
	 */
	protected Config (Class<U> computer, String name, int bits, long initialValue) {
		this.computer = computer;
		this.name = name;
		this.bits = bits;
		this.bytes = bits / 8;
		this.mask = (long)Math.pow(2, bits) - 1;
		this.initialValue = initialValue;
	}
}
