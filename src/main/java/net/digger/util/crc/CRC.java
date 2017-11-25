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
package net.digger.util.crc;

import net.digger.util.crc.computer.Computer;
import net.digger.util.crc.config.CRCConfig;
import net.digger.util.crc.config.ChecksumConfig;
import net.digger.util.crc.config.Config;

/**
 * Calculates a variety of checksums and CRCs.
 * <p>
 * Based on Michael Barr's public domain CRC implementation found at:<br>
 * <a href="http://www.netrino.com/code/crc.zip">http://www.netrino.com/code/crc.zip</a><br>
 * which in turn is inspired by Ross Williams' "Parameterized Model" here:<br>
 * <a href="http://www.ross.net/crc/download/crc_v3.txt">http://www.ross.net/crc/download/crc_v3.txt</a>
 * <p>
 * Various additions and improvements include adding checksum calculation,
 * optional byte reflection (used by Kermit), and incremental crc calculation.
 * <p>
 * Additional CRC parameters from various places, including:<br>
 * <a href="https://stackoverflow.com/questions/4455257/crc16-checksum-hcs08-vs-kermit-vs-xmodem">https://stackoverflow.com/questions/4455257/crc16-checksum-hcs08-vs-kermit-vs-xmodem</a><br>
 * <a href="https://www.lammertbies.nl/comm/info/crc-calculation.html">https://www.lammertbies.nl/comm/info/crc-calculation.html</a>
 * <p>
 * This might be a good source for more CRC parameters:<br>
 * <a href="http://reveng.sourceforge.net/crc-catalogue/">http://reveng.sourceforge.net/crc-catalogue/</a>
 * 
 * @author walton
 * 
 * @param <T> Class of Config instance used by Computer implementation.
 * @param <U> Class of Computer implementation used by Config instance.
 */
public class CRC<T extends Config<U, T>, U extends Computer<T, U>>  {
	// ##### Preconfigured Checksums and CRCs.

	public static final ChecksumConfig Checksum8 = new ChecksumConfig("8-bit Checksum", 8);
	public static final ChecksumConfig Checksum16 = new ChecksumConfig("16-bit Checksum", 16);
	public static final ChecksumConfig Checksum32 = new ChecksumConfig("32-bit Checksum", 32);
	public static final CRCConfig CRC16 = new CRCConfig("CRC-16", 16, 0x8005, 0x0000, 0x0000, true, true, false);
	public static final CRCConfig CRC16_Modbus = new CRCConfig("CRC-16 Modbus", 16, 0x8005, 0xFFFF, 0x0000, true, true, false);
	public static final CRCConfig CRC16_CCITT = new CRCConfig("CRC-CCITT", 16, 0x1021, 0xFFFF, 0x0000, false, false, false);
	public static final CRCConfig CRC16_CCITT_XModem = new CRCConfig("CRC-CCITT XModem", 16, 0x1021, 0x0000, 0x0000, false, false, false);
	public static final CRCConfig CRC16_CCITT_0x1D0F = new CRCConfig("CRC-CCITT 0x1D0F", 16, 0x1021, 0x1D0F, 0x0000, false, false, false);
	public static final CRCConfig CRC16_CCITT_Kermit = new CRCConfig("CRC-CCITT Kermit", 16, 0x1021, 0x0000, 0x0000, true, true, true);
	public static final CRCConfig CRC16_DNP = new CRCConfig("CRC-DNP", 16, 0x3D65, 0x0000, 0xFFFF, true, true, true);
	public static final CRCConfig CRC32 = new CRCConfig("CRC-32", 32, 0x04C11DB7L, 0xFFFFFFFFL, 0xFFFFFFFFL, true, true, false);

	
	// ##### Internal instance data
	
	private final T config;
	private final U computer;

	// ##### Instance constructor

	/**
	 * Create an instance with the given CRC config.
	 * <p>
	 * This is only required to use the faster table-driven method.
	 * 
	 * @param <T> Class of Config instance used by Computer implementation.
	 * @param config CRC configuration to use.
	 */
	public CRC(T config) {
		try {
			this.config = config;
			this.computer = config.computer.newInstance();
			computer.fastInit(config);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to create instance of provided CRC computer class.", e);
		}
	}


	// ##### Static API methods

	/**
	 * Compute the CRC of a given message using the slower computational method.
	 * 
	 * @param <T> Class of Config instance used by Computer implementation.
	 * @param <U> Class of Computer implementation used by Config instance.
	 * @param config CRC configuration to use.
	 * @param message Message to calculate CRC for.
	 * @return The CRC of the message.
	 */
	public static <T extends Config<U, T>, U extends Computer<T, U>> long calculate(T config, String message) {
		return calculate(config, message.getBytes());
	}
	
	/**
	 * Compute the CRC of a given message using the slower computational method.
	 * 
	 * @param <T> Class of Config instance used by Computer implementation.
	 * @param <U> Class of Computer implementation used by Config instance.
	 * @param config CRC configuration to use.
	 * @param message Message to calculate CRC for.
	 * @return The CRC of the message.
	 */
	public static <T extends Config<U, T>, U extends Computer<T, U>> long calculate(T config, byte[] message) {
		try {
			U computer = config.computer.newInstance();
			return computer.slow(config, message);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to create instance of provided CRC computer class.", e);
		}
	}
	
	/**
	 * Update the given CRC with a new byte using the slower computational method.
	 * 
	 * @param <T> Class of Config instance used by Computer implementation.
	 * @param <U> Class of Computer implementation used by Config instance.
	 * @param config CRC configuration to use.
	 * @param crc Current value of the CRC.  Use {@code null} to start.
	 * @param b Next byte of the message to calculate CRC for.
	 * @return The CRC of the message so far.
	 */
	public static <T extends Config<U, T>, U extends Computer<T, U>> long update(T config, Long crc, byte b) {
		try {
			U computer = config.computer.newInstance();
			return computer.slowUpdate(config, crc, b);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to create instance of provided CRC computer class.", e);
		}
	}
	

	// ##### Instance API methods

	/**
	 * Compute the CRC of a given message using the faster table-drive method.
	 * 
	 * @param message Message to calculate CRC for.
	 * @return The CRC of the message.
	 */
	public long calculate(String message) {
		return calculate(message.getBytes());
	}
	
	/**
	 * Compute the CRC of a given message using the faster table-drive method.
	 * 
	 * @param message Message to calculate CRC for.
	 * @return The CRC of the message.
	 */
	public long calculate(byte[] message) {
		return computer.fast(config, message);
	}
	
	/**
	 * Update the given CRC with a new byte using the faster table-driven method.
	 * 
	 * @param crc Current value of the CRC.  Use {@code null} to start.
	 * @param b Next byte of the message to calculate CRC for.
	 * @return The CRC of the message so far.
	 */
	public long update(Long crc, byte b) {
		return computer.fastUpdate(config, crc, b);
	}
}
