package com.slowfrog.qwop.filter;

import com.slowfrog.qwop.RunInfo;

public class CrashedFilter implements IFilter<RunInfo> {

    @Override
    public boolean matches(RunInfo t) {
        return t.isCrashed();
    }


}
