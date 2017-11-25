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

import net.digger.util.crc.config.CRCConfig;

/**
 * CRC computation class.
 * 
 * @author walton
 */
public class CRCComputer implements Computer<CRCConfig, CRCComputer> {
	/**
	 * Lookup table for fast method.
	 */
	private final long[] crcTable = new long[256];

	@Override
	public long slow(CRCConfig config, byte[] message) {
		long crc = config.initialValue;
		// Perform modulo-2 division, a byte at a time.
		for (byte b : message) {
			crc = slowCore(config, crc, b);
		}
		// The final remainder is the CRC result.
		return finalize(config, crc);
	}

	@Override
	public long slowUpdate(CRCConfig config, Long crc, byte b) {
		if (crc == null) {
			crc = config.initialValue;
		} else {
			crc = unfinalize(config, crc);
		}
		crc = slowCore(config, crc, b);
		return finalize(config, crc);
	}

	@Override
	public void fastInit(CRCConfig config) {
		long crc;
		// Compute the remainder of each possible dividend.
		for (char dividend=0; dividend<256; ++dividend) {
			// Start with the dividend followed by zeros.
			crc = (long)dividend << (config.bits - 8);
			crc = fastInitCore(config, crc);
			// Store the result into the table.
			crcTable[dividend] = crc;
		}
	}

	@Override
	public long fast(CRCConfig config, byte[] message) {
		long crc = config.initialValue;
		// Divide the message by the polynomial, a byte at a time.
		for (byte b : message) {
			crc = fastCore(config, crc, b);
		}
		// The final remainder is the CRC.
		return finalize(config, crc);
	}

	@Override
	public long fastUpdate(CRCConfig config, Long crc, byte b) {
		if (crc == null) {
			crc = config.initialValue;
		} else {
			crc = unfinalize(config, crc);
		}
		crc = fastCore(config, crc, b);
		return finalize(config, crc);
	}


	// ##### Internal implementation methods
	
	/**
	 * Main part of the slower computational CRC method.
	 * <p>
	 * Used by {@link #slow(CRCConfig, byte[])} and {@link #slowUpdate(CRCConfig, Long, byte)}.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Current value of the CRC.
	 * @param b Next byte of the message to calculate CRC for.
	 * @return The CRC of the message so far.
	 */
	private long slowCore(CRCConfig config, long crc, byte b) {
		// Bring the next byte into the crc.
		long data = b;
		if (config.reflectInputBits) {
			data = reflectBits(data, 8);
		}
		crc ^= (data << (config.bits - 8));
		crc = fastInitCore(config, crc);
		return crc;
	}

	/**
	 * Main part of the CRC lookup table population.
	 * <p>
	 * Used by {@link #fastInit(CRCConfig)} and {@link #slowCore(CRCConfig, long, byte)}.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Current value of the partial CRC.
	 * @return The partial CRC to add to lookup table.
	 */
	private long fastInitCore(CRCConfig config, long crc) {
		// Perform modulo-2 division, a bit at a time.
		for (char bit=8; bit>0; bit--) {
			// Try to divide the current data bit.
			if ((crc & config.topBit) > 0) {
				crc = (crc << 1) & config.mask;
				crc ^= config.polynomial;
			} else {
				crc <<= 1;
			}
		}
		return crc;
	}
	
	/**
	 * Main part of the faster table-driven CRC method.
	 * <p>
	 * Used by {@link #fast(CRCConfig, byte[])} and {@link #fastUpdate(CRCConfig, Long, byte)}.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Current value of the CRC.
	 * @param b Next byte of the message to calculate CRC for.
	 * @return The CRC of the message so far.
	 */
	private long fastCore(CRCConfig config, long crc, byte b) {
		// Bring the next byte into the crc.
		char data = (char)b;
		if (config.reflectInputBits) {
			data = (char)reflectBits(data, 8);
		}
		data ^= crc >>> (config.bits - 8);
		crc = (crc << 8) & config.mask;
		crc = crcTable[data] ^ crc;
		return crc;
	}

	
	// ##### Utility methods

	/**
	 * Reorder the bits of a binary sequence, by reflecting
	 * them about the middle position.
	 * <p>
	 * No checking is done that nBits <= 32.
	 * 
	 * @param data
	 * @param nBits
	 * @return The reflection of the original data.
	 */
	protected long reflectBits(long data, int nBits) {
		long reflection = 0x00000000;
		// Reflect the data about the center bit.
		for (char bit=0; bit<nBits; bit++) {
			// If the LSB bit is set, set the reflection of it.
			if ((data & 0x01) > 0) {
				reflection |= (1L << ((nBits - 1) - bit));
			}
			data >>>= 1;
		}
		return reflection;
	}
	
	/**
	 * Reorder the bytes of a binary sequence, by reflecting
	 * them about the middle position.
	 * <p>
	 * No checking is done that nBytes <= 4.
	 * 
	 * @param data
	 * @param nBytes
	 * @return The reflection of the original data.
	 */
	protected long reflectBytes(long data, int nBytes) {
		long reflection = 0x00000000;
		// Reflect the data about the center byte.
		for (char nByte=0; nByte<nBytes; nByte++) {
			// Set the reflection of the low byte.
			reflection |= ((data & 0xFF) << (((nBytes - 1) - nByte) * 8));
			data >>>= 8;
		}
		return reflection;
	}
	
	/**
	 * Perform the final steps of the CRC calculation.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Current value of the CRC.
	 * @return Final value of the CRC.
	 */
	protected long finalize(CRCConfig config, long crc) {
		if (config.reflectOutputBits) {
			crc = reflectBits(crc, config.bits);
		}
		crc ^= config.finalXORValue;
		if (config.reflectOutputBytes) {
			crc= reflectBytes(crc, config.bytes);
		}
		return crc;
	}
	
	/**
	 * Undo the final steps of the CRC calculation, for incremental update.
	 * 
	 * @param config CRC configuration to use.
	 * @param crc Previous final value of the CRC.
	 * @return In progress value of the CRC.
	 */
	protected long unfinalize(CRCConfig config, long crc) {
		if (config.reflectOutputBytes) {
			crc = reflectBytes(crc, config.bytes);
		}
		crc ^= config.finalXORValue;
		if (config.reflectOutputBits) {
			crc = reflectBits(crc, config.bits);
		}
		return crc;
	}

}
