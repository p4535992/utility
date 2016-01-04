package com.github.p4535992.util.hibernate.interceptor;

import java.util.Set;

/**
 * Created by 4535992 on 13/05/2015.
 */
public interface DirtyAware {

    Set<String> getDirtyProperties();

    void clearDirtyProperties();
}
