package net.bingyan.android.wificar.queue;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jinge on 2016/3/10.
 */
public class CodeQueue implements Queue<SocketCode> {
    Queue<ModeCode> modeCodes = new LinkedBlockingQueue<>(1);
    Queue<MotorCode> motorCodes = new LinkedBlockingQueue<>(2);
    Queue<AimCode> aimCodes = new LinkedBlockingQueue<>(2);
    Queue<BasicCode> basicCodes = new LinkedBlockingQueue<>(1);

    @Override
    public boolean add(SocketCode socketCode) {
        if (socketCode instanceof ModeCode) {
            if (modeCodes.size() == 1) {
                modeCodes.poll();
            }
            modeCodes.add((ModeCode) socketCode);
            return true;
        } else if (socketCode instanceof MotorCode) {
            if (motorCodes.size() == 2) {
                motorCodes.poll();
            }
            motorCodes.add((MotorCode) socketCode);
            return true;
        } else if (socketCode instanceof AimCode) {
            if (aimCodes.size() == 2) {
                aimCodes.poll();
            }
            aimCodes.add((AimCode) socketCode);
            return true;
        } else if (socketCode instanceof BasicCode) {
            if (basicCodes.size() == 1) {
                basicCodes.poll();
            }
            basicCodes.add((BasicCode) socketCode);
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends SocketCode> collection) {
        return false;
    }

    @Override
    public void clear() {
        modeCodes.clear();
        motorCodes.clear();
        aimCodes.clear();
    }

    @Override
    public boolean contains(Object object) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NonNull
    @Override
    public Iterator<SocketCode> iterator() {
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return null;
    }

    @Override
    public boolean offer(SocketCode socketCode) {
        return false;
    }

    @Override
    public SocketCode remove() {
        return null;
    }

    @Override
    public SocketCode poll() {
        SocketCode socketCode = modeCodes.poll();
        if (socketCode == null) {
            socketCode = motorCodes.poll();
            if (socketCode == null) {
                socketCode = aimCodes.poll();
                if (socketCode == null)
                    socketCode = basicCodes.poll();
            }
        }
        return socketCode;
    }

    @Override
    public SocketCode element() {
        return null;
    }

    @Override
    public SocketCode peek() {
        return null;
    }
}
