/**
 *
 */
package org.funsoft.remoteagent.installer.core;

/**
 * @author htb
 */
public abstract class AbstractCompositeInstaller implements ICompositeInstaller {
    private final String name;
    private final String description;

    protected AbstractCompositeInstaller(String name) {
        this(name, "");
    }

    protected AbstractCompositeInstaller(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void execute() throws Exception {
    }

    @Override
    public final String getDescription() {
        return description;
    }
}
