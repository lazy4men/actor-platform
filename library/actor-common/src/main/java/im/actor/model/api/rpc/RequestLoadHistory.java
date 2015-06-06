package im.actor.model.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import im.actor.model.droidkit.bser.Bser;
import im.actor.model.droidkit.bser.BserParser;
import im.actor.model.droidkit.bser.BserObject;
import im.actor.model.droidkit.bser.BserValues;
import im.actor.model.droidkit.bser.BserWriter;
import im.actor.model.droidkit.bser.DataInput;
import im.actor.model.droidkit.bser.DataOutput;
import im.actor.model.droidkit.bser.util.SparseArray;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.j2objc.annotations.ObjectiveCName;
import static im.actor.model.droidkit.bser.Utils.*;
import java.io.IOException;
import im.actor.model.network.parser.*;
import java.util.List;
import java.util.ArrayList;
import im.actor.model.api.*;

public class RequestLoadHistory extends Request<ResponseLoadHistory> {

    public static final int HEADER = 0x76;
    public static RequestLoadHistory fromBytes(byte[] data) throws IOException {
        return Bser.parse(new RequestLoadHistory(), data);
    }

    private OutPeer peer;
    private long minDate;
    private int limit;

    public RequestLoadHistory(@NotNull OutPeer peer, long minDate, int limit) {
        this.peer = peer;
        this.minDate = minDate;
        this.limit = limit;
    }

    public RequestLoadHistory() {

    }

    @NotNull
    public OutPeer getPeer() {
        return this.peer;
    }

    public long getMinDate() {
        return this.minDate;
    }

    public int getLimit() {
        return this.limit;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.peer = values.getObj(1, new OutPeer());
        this.minDate = values.getLong(3);
        this.limit = values.getInt(4);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.peer == null) {
            throw new IOException();
        }
        writer.writeObject(1, this.peer);
        writer.writeLong(3, this.minDate);
        writer.writeInt(4, this.limit);
    }

    @Override
    public String toString() {
        String res = "rpc LoadHistory{";
        res += "peer=" + this.peer;
        res += ", minDate=" + this.minDate;
        res += ", limit=" + this.limit;
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
