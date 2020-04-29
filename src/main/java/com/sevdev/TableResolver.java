package com.sevdev;
import com.sevdev.Table;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class TableResolver implements ContextResolver<Table> {
    private Table table;

    public TableResolver() {
        table = new Table();
    }

    @Override
    public Table getContext(Class<?> type) {
        return table;
    }
}
