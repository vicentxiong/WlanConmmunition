package com.xiong.wlanconmmunition;

/**
 *upd报文的结构如下：
 *报文头           1 byte 0x00
 *数据域标识位      2 byte 前1位标识接收发送，后15位代表不同的数据区域是否存有对应数据
 *{
 *  1 bit :
 *  2 bit : packet id area
 *  3 bit : sender name area
 *  4 bit : sender host name area
 *  5 bit : text area
 *  6 bit : image area
 *  7 bit : audio area
 *  8 bit : video area
 *  9 bit : file transaction area
 *  10 bit : audio communication area
 *  11 bit : vide communication area
 *  ......后面位为扩展标识
 *}
 *
 * 每一个数据域的格式为 length data
 * length ： 2 byte
 *
 *报文lrc校验       1 byte 除报文头尾之外的所有字节数据做异或
 *报文尾           1 byte 0x00
 *
 */
public class DataPacketProtocol {
	public static final char PROTOCOL_SEPARATOR = ':';
	
	public String packetId; // 鍖呯紪鍙�?	
	public String senderName; // 鍙戦�浜哄鍚�	
	public String senderHostName; // 鍙戦�涓绘満鍚�	
	public int command; // 鍛戒护�?��	
	public String content; // 闄勫姞淇℃伅
	private String destIp; // 鐩殑ip
	private String srcIp; // 婧恑p

	public byte[] getByte() {
		StringBuffer protocolStr = new StringBuffer();
		protocolStr.append(packetId).append(PROTOCOL_SEPARATOR)
		           .append(senderName).append(PROTOCOL_SEPARATOR)
				   .append(senderHostName).append(PROTOCOL_SEPARATOR)
				   .append(command).append(PROTOCOL_SEPARATOR)
				   .append(content);
		return protocolStr.toString().getBytes();
	}

	public void sync(byte[] bytes,int length) {
		String rcvDataPacket = new String(bytes,0,length);
		String[] fields = rcvDataPacket.split(Character.toString(PROTOCOL_SEPARATOR));
		this.packetId = fields[0];
		this.senderName = fields[1];
		this.senderHostName = fields[2];
		this.command = Integer.parseInt(fields[3]);
		StringBuffer sb = new StringBuffer();
		for (int i = 4; i < fields.length; i++) {
			sb.append(fields[i]);
		}
		this.content = sb.toString();
	}

	public String getDestIp() {
		return destIp;
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

}
