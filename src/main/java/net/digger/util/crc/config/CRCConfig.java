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

import net.digger.util.crc.computer.CRCComputer;

/**
 * CRC configuration class.
 */
public class CRCConfig extends Config<CRCComputer, CRCConfig> {
	/**
	 * Polynomial for this CRC.
	 */
	public final long polynomial;
	/**
	 * XOR value to apply to the final CRC.
	 */
	public final long finalXORValue;
	/**
	 * Reverse the bits in the input bytes?
	 */
	public final boolean reflectInputBits;
	/**
	 * Reverse the bits in the output bytes?
	 */
	public final boolean reflectOutputBits;
	/**
	 * Reverse the output bytes?
	 */
	public final boolean reflectOutputBytes;
	/**
	 * Highest bit in the final CRC.
	 */
	public final long topBit;
	
	/**
	 * Create a new CRC configuration.
	 * 
	 * @param name Name of this CRC configuration.
	 * @param bits Number of bits in final output.
	 * @param polynomial Polynomial for this CRC.
	 * @param initialValue Initial value for this CRC configuration.
	 * @param finalXORValue XOR value to apply to the final CRC.
	 * @param reflectInputBits Reverse the bits in the input bytes?
	 * @param reflectOutputBits Reverse the bits in the output bytes?
	 * @param reflectOutputBytes Reverse the output bytes?
	 */
	public CRCConfig(String name, int bits,
			long polynomial, long initialValue, long finalXORValue,
			boolean reflectInputBits, boolean reflectOutputBits, boolean reflectOutputBytes) {
		super(CRCComputer.class, name, bits, initialValue);
		this.polynomial = polynomial;
		this.finalXORValue = finalXORValue;
		this.reflectInputBits = reflectInputBits;
		this.reflectOutputBits = reflectOutputBits;
		this.reflectOutputBytes = reflectOutputBytes;
		this.topBit = 1 << (bits - 1);
	}
}
