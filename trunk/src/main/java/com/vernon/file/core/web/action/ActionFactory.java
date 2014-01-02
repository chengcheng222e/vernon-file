package com.vernon.file.core.web.action;

import com.vernon.file.action.ImageAction;
import com.vernon.file.action.NotAllowAction;
import com.vernon.file.core.FileType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public class ActionFactory {

    private static Map<FileType, Action> CMDS = new HashMap<FileType, Action>();

    static {
        CMDS.put(FileType.IMAGE, new ImageAction());
        CMDS.put(FileType.AUDIO, new NotAllowAction());
        CMDS.put(FileType.VIDEO, new NotAllowAction());
        CMDS.put(FileType.OTHER, new NotAllowAction());
        CMDS.put(FileType.NOTALLOW, new NotAllowAction());
    }

    public static Action getAction(FileType fileType) {
        return CMDS.get(fileType);
    }
}
