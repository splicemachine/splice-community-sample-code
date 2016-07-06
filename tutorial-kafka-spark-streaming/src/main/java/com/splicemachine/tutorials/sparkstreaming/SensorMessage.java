package com.splicemachine.tutorials.sparkstreaming;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.Connection;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.types.SQLDecimal;
import com.splicemachine.db.iapi.types.SQLDouble;
import com.splicemachine.db.iapi.types.SQLInteger;
import com.splicemachine.db.iapi.types.SQLLongint;
import com.splicemachine.db.iapi.types.SQLTimestamp;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.sql.execute.ValueRow;

public class SensorMessage {

    private int num_access_files;
    private long src_bytes;
    private int srv_count;
    private int num_compromised;
    private double rerror_rate;
    private int urgent;
    private double dst_host_same_srv_rate;
    private int duration;
    private String label;
    private double srv_rerror_rate;
    private double srv_serror_rate;
    private int is_host_login;
    private int wrong_fragment;
    private String uuid;
    private String service;
    private double serror_rate;
    private int num_outbound_cmds;
    private int is_guest_login;
    private double dst_host_rerror_rate;
    private double dst_host_srv_serror_rate;
    private double diff_srv_rate;
    private int hot;
    private int dst_host_srv_count;
    private int logged_in;
    private int num_shells;
    private double dst_host_srv_diff_host_rate;
    private BigInteger index;
    private double srv_diff_host_rate;
    private double dst_host_same_src_port_rate;
    private int root_shell;
    private String flag;
    private int su_attempted;
    private int dst_host_count;
    private int num_file_creations;
    private String protocol_type;
    private int count;
    private Timestamp utc;
    private int land;
    private double same_srv_rate;
    private int dst_bytes;
    private long sequence_id;
    private double dst_host_diff_srv_rate;
    private double dst_host_srv_rerror_rate;
    private int num_root;
    private int num_failed_logins;
    private int dst_host_serror_rate;
    public int getNum_access_files() {
        return num_access_files;
    }
    public void setNum_access_files(int num_access_files) {
        this.num_access_files = num_access_files;
    }
    public long getSrc_bytes() {
        return src_bytes;
    }
    public void setSrc_bytes(long src_bytes) {
        this.src_bytes = src_bytes;
    }
    public int getSrv_count() {
        return srv_count;
    }
    public void setSrv_count(int srv_count) {
        this.srv_count = srv_count;
    }
    public int getNum_compromised() {
        return num_compromised;
    }
    public void setNum_compromised(int num_compromised) {
        this.num_compromised = num_compromised;
    }
    public double getRerror_rate() {
        return rerror_rate;
    }
    public void setRerror_rate(double rerror_rate) {
        this.rerror_rate = rerror_rate;
    }
    public int getUrgent() {
        return urgent;
    }
    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }
    public double getDst_host_same_srv_rate() {
        return dst_host_same_srv_rate;
    }
    public void setDst_host_same_srv_rate(double dst_host_same_srv_rate) {
        this.dst_host_same_srv_rate = dst_host_same_srv_rate;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public double getSrv_rerror_rate() {
        return srv_rerror_rate;
    }
    public void setSrv_rerror_rate(double srv_rerror_rate) {
        this.srv_rerror_rate = srv_rerror_rate;
    }
    public double getSrv_serror_rate() {
        return srv_serror_rate;
    }
    public void setSrv_serror_rate(double srv_serror_rate) {
        this.srv_serror_rate = srv_serror_rate;
    }
    public int getIs_host_login() {
        return is_host_login;
    }
    public void setIs_host_login(int is_host_login) {
        this.is_host_login = is_host_login;
    }
    public int getWrong_fragment() {
        return wrong_fragment;
    }
    public void setWrong_fragment(int wrong_fragment) {
        this.wrong_fragment = wrong_fragment;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public double getSerror_rate() {
        return serror_rate;
    }
    public void setSerror_rate(double serror_rate) {
        this.serror_rate = serror_rate;
    }
    public int getNum_outbound_cmds() {
        return num_outbound_cmds;
    }
    public void setNum_outbound_cmds(int num_outbound_cmds) {
        this.num_outbound_cmds = num_outbound_cmds;
    }
    public int getIs_guest_login() {
        return is_guest_login;
    }
    public void setIs_guest_login(int is_guest_login) {
        this.is_guest_login = is_guest_login;
    }
    public double getDst_host_rerror_rate() {
        return dst_host_rerror_rate;
    }
    public void setDst_host_rerror_rate(double dst_host_rerror_rate) {
        this.dst_host_rerror_rate = dst_host_rerror_rate;
    }
    public double getDst_host_srv_serror_rate() {
        return dst_host_srv_serror_rate;
    }
    public void setDst_host_srv_serror_rate(double dst_host_srv_serror_rate) {
        this.dst_host_srv_serror_rate = dst_host_srv_serror_rate;
    }
    public double getDiff_srv_rate() {
        return diff_srv_rate;
    }
    public void setDiff_srv_rate(double diff_srv_rate) {
        this.diff_srv_rate = diff_srv_rate;
    }
    public int getHot() {
        return hot;
    }
    public void setHot(int hot) {
        this.hot = hot;
    }
    public int getDst_host_srv_count() {
        return dst_host_srv_count;
    }
    public void setDst_host_srv_count(int dst_host_srv_count) {
        this.dst_host_srv_count = dst_host_srv_count;
    }
    public int getLogged_in() {
        return logged_in;
    }
    public void setLogged_in(int logged_in) {
        this.logged_in = logged_in;
    }
    public int getNum_shells() {
        return num_shells;
    }
    public void setNum_shells(int num_shells) {
        this.num_shells = num_shells;
    }
    public double getDst_host_srv_diff_host_rate() {
        return dst_host_srv_diff_host_rate;
    }
    public void setDst_host_srv_diff_host_rate(double dst_host_srv_diff_host_rate) {
        this.dst_host_srv_diff_host_rate = dst_host_srv_diff_host_rate;
    }
    public BigInteger getIndex() {
        return index;
    }
    public void setIndex(BigInteger index) {
        this.index = index;
    }
    public double getSrv_diff_host_rate() {
        return srv_diff_host_rate;
    }
    public void setSrv_diff_host_rate(double srv_diff_host_rate) {
        this.srv_diff_host_rate = srv_diff_host_rate;
    }
    public double getDst_host_same_src_port_rate() {
        return dst_host_same_src_port_rate;
    }
    public void setDst_host_same_src_port_rate(double dst_host_same_src_port_rate) {
        this.dst_host_same_src_port_rate = dst_host_same_src_port_rate;
    }
    public int getRoot_shell() {
        return root_shell;
    }
    public void setRoot_shell(int root_shell) {
        this.root_shell = root_shell;
    }
    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public int getSu_attempted() {
        return su_attempted;
    }
    public void setSu_attempted(int su_attempted) {
        this.su_attempted = su_attempted;
    }
    public int getDst_host_count() {
        return dst_host_count;
    }
    public void setDst_host_count(int dst_host_count) {
        this.dst_host_count = dst_host_count;
    }
    public int getNum_file_creations() {
        return num_file_creations;
    }
    public void setNum_file_creations(int num_file_creations) {
        this.num_file_creations = num_file_creations;
    }
    public String getProtocol_type() {
        return protocol_type;
    }
    public void setProtocol_type(String protocol_type) {
        this.protocol_type = protocol_type;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public Timestamp getUtc() {
        return utc;
    }
    public void setUtc(Timestamp utc) {
        this.utc = utc;
    }
    public int getLand() {
        return land;
    }
    public void setLand(int land) {
        this.land = land;
    }
    public double getSame_srv_rate() {
        return same_srv_rate;
    }
    public void setSame_srv_rate(double same_srv_rate) {
        this.same_srv_rate = same_srv_rate;
    }
    public int getDst_bytes() {
        return dst_bytes;
    }
    public void setDst_bytes(int dst_bytes) {
        this.dst_bytes = dst_bytes;
    }
    public long getSequence_id() {
        return sequence_id;
    }
    public void setSequence_id(long sequence_id) {
        this.sequence_id = sequence_id;
    }
    public double getDst_host_diff_srv_rate() {
        return dst_host_diff_srv_rate;
    }
    public void setDst_host_diff_srv_rate(double dst_host_diff_srv_rate) {
        this.dst_host_diff_srv_rate = dst_host_diff_srv_rate;
    }
    public double getDst_host_srv_rerror_rate() {
        return dst_host_srv_rerror_rate;
    }
    public void setDst_host_srv_rerror_rate(double dst_host_srv_rerror_rate) {
        this.dst_host_srv_rerror_rate = dst_host_srv_rerror_rate;
    }
    public int getNum_root() {
        return num_root;
    }
    public void setNum_root(int num_root) {
        this.num_root = num_root;
    }
    public int getNum_failed_logins() {
        return num_failed_logins;
    }
    public void setNum_failed_logins(int num_failed_logins) {
        this.num_failed_logins = num_failed_logins;
    }
    public int getDst_host_serror_rate() {
        return dst_host_serror_rate;
    }
    public void setDst_host_serror_rate(int dst_host_serror_rate) {
        this.dst_host_serror_rate = dst_host_serror_rate;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("num_access_files=["+num_access_files +"]\n");
        sb.append("src_bytes=["+src_bytes +"]\n");
        sb.append("srv_count=["+srv_count +"]\n");
        sb.append("num_compromised=["+num_compromised +"]\n");
        sb.append("rerror_rate=["+rerror_rate +"]\n");
        sb.append("urgent=["+urgent +"]\n");
        sb.append("dst_host_same_srv_rate=["+dst_host_same_srv_rate +"]\n");
        sb.append("duration=["+duration +"]\n");
        sb.append("label=["+label +"]\n");
        sb.append("srv_rerror_rate=["+srv_rerror_rate +"]\n");
        sb.append("srv_serror_rate=["+srv_serror_rate +"]\n");
        sb.append("is_host_login=["+is_host_login +"]\n");
        sb.append("wrong_fragment=["+wrong_fragment +"]\n");
        sb.append("uuid=["+uuid +"]\n");
        sb.append("service=["+service +"]\n");
        sb.append("serror_rate=["+serror_rate +"]\n");
        sb.append("num_outbound_cmds=["+num_outbound_cmds +"]\n");
        sb.append("is_guest_login=["+is_guest_login +"]\n");
        sb.append("dst_host_rerror_rate=["+dst_host_rerror_rate +"]\n");
        sb.append("dst_host_srv_serror_rate=["+dst_host_srv_serror_rate +"]\n");
        sb.append("diff_srv_rate=["+diff_srv_rate +"]\n");
        sb.append("hot=["+hot +"]\n");
        sb.append("dst_host_srv_count=["+dst_host_srv_count +"]\n");
        sb.append("logged_in=["+logged_in +"]\n");
        sb.append("num_shells=["+num_shells +"]\n");
        sb.append("dst_host_srv_diff_host_rate=["+dst_host_srv_diff_host_rate +"]\n");
        sb.append("index=["+index +"]\n");
        sb.append("srv_diff_host_rate=["+srv_diff_host_rate +"]\n");
        sb.append("dst_host_same_src_port_rate=["+dst_host_same_src_port_rate +"]\n");
        sb.append("root_shell=["+root_shell +"]\n");
        sb.append("flag=["+flag +"]\n");
        sb.append("su_attempted=["+su_attempted +"]\n");
        sb.append("dst_host_count=["+dst_host_count +"]\n");
        sb.append("num_file_creations=["+num_file_creations +"]\n");
        sb.append("protocol_type=["+protocol_type +"]\n");
        sb.append("count=["+count +"]\n");
        sb.append("utc=["+utc +"]\n");
        sb.append("land=["+land +"]\n");
        sb.append("same_srv_rate=["+same_srv_rate +"]\n");
        sb.append("dst_bytes=["+dst_bytes +"]\n");
        sb.append("sequence_id=["+sequence_id +"]\n");
        sb.append("dst_host_diff_srv_rate=["+dst_host_diff_srv_rate +"]\n");
        sb.append("dst_host_srv_rerror_rate=["+dst_host_srv_rerror_rate +"]\n");
        sb.append("num_root=["+num_root +"]\n");
        sb.append("num_failed_logins=["+num_failed_logins +"]\n");
        sb.append("dst_host_serror_rate=["+dst_host_serror_rate +"]\n");
        return sb.toString();
    }
    
    public void addParametersToPreparedStatement(PreparedStatement stmt) throws SQLException {
        
        stmt.setInt(1,num_access_files);
        stmt.setLong(2,src_bytes);
        stmt.setInt(3,srv_count);
        stmt.setInt(4,num_compromised);
        stmt.setDouble(5,rerror_rate);
        stmt.setInt(6,urgent);
        stmt.setDouble(7,dst_host_same_srv_rate);
        stmt.setInt(8,duration);
        stmt.setString(9,label);
        stmt.setDouble(10,srv_rerror_rate);
        stmt.setDouble(11,srv_serror_rate);
        stmt.setInt(12,is_host_login);
        stmt.setInt(13,wrong_fragment);
        stmt.setString(14,uuid);
        stmt.setString(15,service);
        stmt.setDouble(16,serror_rate);
        stmt.setInt(17,num_outbound_cmds);
        stmt.setInt(18,is_guest_login);
        stmt.setDouble(19,dst_host_rerror_rate);
        stmt.setDouble(20,dst_host_srv_serror_rate);
        stmt.setDouble(21,diff_srv_rate);
        stmt.setInt(22,hot);
        stmt.setInt(23,dst_host_srv_count);
        stmt.setInt(24,logged_in);
        stmt.setInt(25,num_shells);
        stmt.setDouble(26,dst_host_srv_diff_host_rate);
        stmt.setBigDecimal(27, new BigDecimal(index));
        stmt.setDouble(28,srv_diff_host_rate);
        stmt.setDouble(29,dst_host_same_src_port_rate);
        stmt.setInt(30,root_shell);
        stmt.setString(31,flag);
        stmt.setInt(32,su_attempted);
        stmt.setInt(33,dst_host_count);
        stmt.setInt(34,num_file_creations);
        stmt.setString(35,protocol_type);
        stmt.setInt(36,count);
        stmt.setTimestamp(37,utc);
        stmt.setInt(38,land);
        stmt.setDouble(39,same_srv_rate);
        stmt.setInt(40,dst_bytes);
        stmt.setLong(41,sequence_id);
        stmt.setDouble(42,dst_host_diff_srv_rate);
        stmt.setDouble(43,dst_host_srv_rerror_rate);
        stmt.setInt(44,num_root);
        stmt.setInt(45,num_failed_logins);
        stmt.setInt(46,dst_host_serror_rate);
    }
    
    public static PreparedStatement getPreparedStatement(Connection con) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into iot.SENSOR_MESSAGES values(");
        for(int i=0; i<46; i++) {
            if(i > 0) {
                sb.append(",");
            }
            sb.append("?");
            
        }   
        sb.append(")");
        return con.prepareStatement(sb.toString());
    }
    
    public static String getTableDefinition() {
        return 
        "num_access_files int, " +
        "src_bytes bigint, " +
        "srv_count int, " +
        "num_compromised int, " +
        "rerror_rate double, " +
        "urgent int, " +
        "dst_host_same_srv_rate double, " +
        "duration int, " +
        "label varchar(50), " +
        "srv_rerror_rate double, " +
        "srv_serror_rate double, " +
        "is_host_login int, " +
        "wrong_fragment int, " +
        "uuid varchar(50), " +
        "service varchar(10), " +
        "serror_rate double, " +
        "num_outbound_cmds int, " +
        "is_guest_login int, " +
        "dst_host_rerror_rate double, " +
        "dst_host_srv_serror_rate double, " +
        "diff_srv_rate double, " +
        "hot int, " +
        "dst_host_srv_count int, " +
        "logged_in int, " +
        "num_shells int, " +
        "dst_host_srv_diff_host_rate double, " +
        "index bigint, " +
        "srv_diff_host_rate double, " +
        "dst_host_same_src_port_rate double, " +
        "root_shell int, " +
        "flag varchar(10), " +
        "su_attempted int, " +
        "dst_host_count int, " +
        "num_file_creations int, " +
        "protocol_type varchar(10), " +
        "count int, " +
        "utc timestamp, " +
        "land int, " +
        "same_srv_rate double, " +
        "dst_bytes int, " +
        "sequence_id bigint, " +
        "dst_host_diff_srv_rate double, " +
        "dst_host_srv_rerror_rate double, " +
        "num_root int, " +
        "num_failed_logins int, " +
        "dst_host_serror_rate int";
    }
    
    public String[] getDataAsStringArray() {
        String[] rtnVal = new String[46];
        rtnVal[0]="" + num_access_files;
        rtnVal[1]="" + src_bytes;
        rtnVal[2]="" + srv_count;
        rtnVal[3]="" + num_compromised;
        rtnVal[4]="" + rerror_rate;
        rtnVal[5]="" + urgent;
        rtnVal[6]="" + dst_host_same_srv_rate;
        rtnVal[7]="" + duration;
        rtnVal[8]=label;
        rtnVal[9]="" + srv_rerror_rate;
        rtnVal[10]="" + srv_serror_rate;
        rtnVal[11]="" + is_host_login;
        rtnVal[12]="" + wrong_fragment;
        rtnVal[13]=uuid;
        rtnVal[14]=service;
        rtnVal[15]="" + serror_rate;
        rtnVal[16]="" + num_outbound_cmds;
        rtnVal[17]="" + is_guest_login;
        rtnVal[18]="" + dst_host_rerror_rate;
        rtnVal[19]="" + dst_host_srv_serror_rate;
        rtnVal[20]="" + diff_srv_rate;
        rtnVal[21]="" + hot;
        rtnVal[22]="" + dst_host_srv_count;
        rtnVal[23]="" + logged_in;
        rtnVal[24]="" + num_shells;
        rtnVal[25]="" + dst_host_srv_diff_host_rate;
        rtnVal[26]=index.toString();
        rtnVal[27]="" + srv_diff_host_rate;
        rtnVal[28]="" + dst_host_same_src_port_rate;
        rtnVal[29]="" + root_shell;
        rtnVal[30]=flag;
        rtnVal[31]="" + su_attempted;
        rtnVal[32]="" + dst_host_count;
        rtnVal[33]="" + num_file_creations;
        rtnVal[34]=protocol_type;
        rtnVal[35]="" + count;
        rtnVal[36]="" + utc;
        rtnVal[37]="" + land;
        rtnVal[38]="" + same_srv_rate;
        rtnVal[39]="" + dst_bytes;
        rtnVal[40]="" + sequence_id;
        rtnVal[41]="" + dst_host_diff_srv_rate;
        rtnVal[42]="" + dst_host_srv_rerror_rate;
        rtnVal[43]="" + num_root;
        rtnVal[44]="" + num_failed_logins;
        rtnVal[45]="" + dst_host_serror_rate;
        
        return rtnVal;
    }
    
    public ValueRow getRow() throws SQLException, StandardException {
        
        ValueRow valueRow = new ValueRow(46);
        valueRow.setColumn(1,new SQLInteger(num_access_files));
        valueRow.setColumn(2,new SQLLongint(src_bytes));
        valueRow.setColumn(3,new SQLInteger(srv_count));
        valueRow.setColumn(4,new SQLInteger(num_compromised));
        valueRow.setColumn(5,new SQLDouble(rerror_rate));
        valueRow.setColumn(6,new SQLInteger(urgent));
        valueRow.setColumn(7,new SQLDouble(dst_host_same_srv_rate));
        valueRow.setColumn(8,new SQLInteger(duration));
        valueRow.setColumn(9,new SQLVarchar(label));
        valueRow.setColumn(10,new SQLDouble(srv_rerror_rate));
        valueRow.setColumn(11,new SQLDouble(srv_serror_rate));
        valueRow.setColumn(12,new SQLInteger(is_host_login));
        valueRow.setColumn(13,new SQLInteger(wrong_fragment));
        valueRow.setColumn(14,new SQLVarchar(uuid));
        valueRow.setColumn(15,new SQLVarchar(service));
        valueRow.setColumn(16,new SQLDouble(serror_rate));
        valueRow.setColumn(17,new SQLInteger(num_outbound_cmds));
        valueRow.setColumn(18,new SQLInteger(is_guest_login));
        valueRow.setColumn(19,new SQLDouble(dst_host_rerror_rate));
        valueRow.setColumn(20,new SQLDouble(dst_host_srv_serror_rate));
        valueRow.setColumn(21,new SQLDouble(diff_srv_rate));
        valueRow.setColumn(22,new SQLInteger(hot));
        valueRow.setColumn(23,new SQLInteger(dst_host_srv_count));
        valueRow.setColumn(24,new SQLInteger(logged_in));
        valueRow.setColumn(25,new SQLInteger(num_shells));
        valueRow.setColumn(26,new SQLDouble(dst_host_srv_diff_host_rate));
        valueRow.setColumn(27, new SQLDecimal(new BigDecimal(index)));
        valueRow.setColumn(28,new SQLDouble(srv_diff_host_rate));
        valueRow.setColumn(29,new SQLDouble(dst_host_same_src_port_rate));
        valueRow.setColumn(30,new SQLInteger(root_shell));
        valueRow.setColumn(31,new SQLVarchar(flag));
        valueRow.setColumn(32,new SQLInteger(su_attempted));
        valueRow.setColumn(33,new SQLInteger(dst_host_count));
        valueRow.setColumn(34,new SQLInteger(num_file_creations));
        valueRow.setColumn(35,new SQLVarchar(protocol_type));
        valueRow.setColumn(36,new SQLInteger(count));
        valueRow.setColumn(37,new SQLTimestamp(utc));
        valueRow.setColumn(38,new SQLInteger(land));
        valueRow.setColumn(39,new SQLDouble(same_srv_rate));
        valueRow.setColumn(40,new SQLInteger(dst_bytes));
        valueRow.setColumn(41,new SQLLongint(sequence_id));
        valueRow.setColumn(42,new SQLDouble(dst_host_diff_srv_rate));
        valueRow.setColumn(43,new SQLDouble(dst_host_srv_rerror_rate));
        valueRow.setColumn(44,new SQLInteger(num_root));
        valueRow.setColumn(45,new SQLInteger(num_failed_logins));
        valueRow.setColumn(46,new SQLInteger(dst_host_serror_rate));
        return valueRow;
    }
    
}
