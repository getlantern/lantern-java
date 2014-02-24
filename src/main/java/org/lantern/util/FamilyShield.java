package org.lantern.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * Encapsulates DNS operations using the <a href=
 * "http://www.opendns.com/home-internet-security/parental-controls/opendns-familyshield/"
 * >Family Shield</a> service from OpenDNS.
 */
public class FamilyShield {
    private static final String[] FAMILYSHIELD_DNS_SERVERS =
            new String[] { "208.67.222.123", "208.67.220.123" };
    private static final String FAMILYSHIELD_BLOCKED_HOST =
            "hit-adult.opendns.com.";

    /**
     * Look up the given host using the Family Shield DNS servers.
     * 
     * @param host
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress lookup(String host) throws UnknownHostException {
        Resolver resolver = new ExtendedResolver(FAMILYSHIELD_DNS_SERVERS);
        try {
            Lookup lookup = new Lookup(host);
            lookup.setResolver(resolver);
            Record[] records = lookup.run();
            ARecord a = (ARecord) records[0];
            return InetAddress.getByAddress(host, a.getAddress().getAddress());
        } catch (TextParseException tpe) {
            throw new UnknownHostException("Invalid hostname");
        }
    }

    /**
     * Check whether the given host is blocked by the Family Shield DNS servers.
     * 
     * @param host
     * @return
     */
    public static boolean isBlocked(String host) {
        try {
            InetAddress address = lookup(host);
            String reverseHost = reverseLookup(address);
            return FAMILYSHIELD_BLOCKED_HOST.equals(reverseHost);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Courtesy of this <a href=
     * "http://www.oreillynet.com/onjava/blog/2005/11/reverse_dns_lookup_and_java.html>O'Reill
     * y article</a>.
     * 
     * @param hostIp
     * @return
     * @throws IOException
     */
    private static String reverseLookup(InetAddress address) throws IOException {
        String hostIp = address.getHostAddress();
        Resolver res = new ExtendedResolver();

        Name name = ReverseMap.fromAddress(hostIp);
        int type = Type.PTR;
        int dclass = DClass.IN;
        Record rec = Record.newRecord(name, type, dclass);
        Message query = Message.newQuery(rec);
        Message response = res.send(query);

        Record[] answers = response.getSectionArray(Section.ANSWER);
        if (answers.length == 0)
            return hostIp;
        else
            return answers[0].rdataToString();
    }
}