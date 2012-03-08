package azkaban.permission;

public class Permission {
    private boolean hasWritePermission;
    private boolean hasReadPermission;
    private boolean hasExecutePermission;
    
    public Permission(boolean read, boolean write, boolean execute) {
        hasReadPermission = read;
        setWritePermission(write);
        hasExecutePermission = execute;
    }

    public boolean hasWritePermission() {
        return hasWritePermission;
    }

    public void setWritePermission(boolean hasWritePermission) {
        this.hasWritePermission = hasWritePermission;
    }
    
    public boolean hasReadPermission() {
        return hasReadPermission;
    }

    public void setReadPermission(boolean hasReadPermission) {
        this.hasReadPermission = hasReadPermission;
    }
    
    public boolean hasExecutePermission() {
        return hasExecutePermission;
    }

    public void setExecutionPermission(boolean hasExecutePermission) {
        this.hasExecutePermission = hasExecutePermission;
    }
}
