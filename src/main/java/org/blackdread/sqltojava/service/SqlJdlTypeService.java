package org.blackdread.sqltojava.service;

import static org.blackdread.sqltojava.entity.JdlFieldEnum.UNSUPPORTED;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.blackdread.sqltojava.entity.JdlFieldEnum;
import org.blackdread.sqltojava.entity.SqlColumn;
import org.blackdread.sqltojava.service.logic.JdlService;
import org.blackdread.sqltojava.util.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SqlJdlTypeService {
    Map<String, JdlFieldEnum> getTypeMap();
    static final Logger log = LoggerFactory.getLogger(SqlJdlTypeService.class);

    default JdlFieldEnum sqlTypeToJdlType(final String sqlType) {
        String typeName = SqlUtils.parseSqlType(sqlType);
        JdlFieldEnum jdlType = Optional.ofNullable(getTypeMap().get(typeName)).orElse(UNSUPPORTED);
        //orElseThrow(() -> new IllegalStateException("Unknown type: " + typeName));
        log.info("SQL type: {} ({}) converted to JDL type: {}", sqlType, typeName, jdlType);
        return jdlType;
    }

    default Map<String, JdlFieldEnum> mergeOverrides(
        final Map<String, JdlFieldEnum> defaultTypeMap,
        final Map<String, JdlFieldEnum> overrides
    ) {
        Map<String, JdlFieldEnum> typeMap = new HashMap<>(defaultTypeMap);
        overrides.forEach((k, v) -> typeMap.merge(k, v, (v1, v2) -> v2));
        return ImmutableMap.copyOf(typeMap);
    }

    default Integer calculateStringMaxLength(SqlColumn column) {
        return SqlUtils.parseSqlSize(column.getType()).orElse(255);
    }
}
