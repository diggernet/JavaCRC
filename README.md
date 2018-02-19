# JavaCRC
JavaCRC is a parameterized checksum and CRC calculation tool, which supports
many different algorithms and parameters.  It is pre-configured with:

* 8-bit checksum
* 16-bit checksum
* 32-bit checksum
* CRC-16
* CRC-16 Modbus
* CRC-16 CCITT
* CRC-16 CCITT (0x1D0F)
* CRC-16 CCITT (Kermit)
* CRC-16 CCITT (XModem)
* CRC-16 DNP
* CRC-32

## Maven configuration

		<dependency>
			<groupId>net.digger</groupId>
			<artifactId>crc-util</artifactId>
			<version>1.0.0</version>
		</dependency>

## Usage
TL;DR: Give it a configuration and a message, get back a number.

* Can be called statically, for simple occasional use:

		String message = "...";
		long result = CRC.calculate(CRC.CRC16, message);

* For more intensive use, it's faster to create an instance and use the
table-driven method:

		CRC<CRCConfig, CRCComputer> crc = new CRC<>(CRC.CRC16);
		String message = "...";
		long result = crc.calculate(message);

* You can also calculate the CRC incrementally:

		String message = "...";
		Long result = null;
		for (byte b : message.getBytes()) {
			result = CRC.update(CRC.CRC16, result, b);
		}

		CRC<CRCConfig, CRCComputer> crc = new CRC<>(CRC.CRC16);
		String message = "...";
		Long result = null;
		for (byte b : message.getBytes()) {
			result = crc.update(result, b);
		}

## License
JavaCRC is provided under the terms of the GNU LGPLv3.
