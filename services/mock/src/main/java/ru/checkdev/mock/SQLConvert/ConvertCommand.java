package ru.checkdev.mock.SQLConvert;

import liquibase.changelog.ChangeLogParameters;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.ChangeLogSerializerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Утилита для перевода скриптов SQL в YML. После применения в полученном файле YML вручную
 * проставить id changeSet.
 */
public class ConvertCommand implements Callable<Void> {

    @Override
    public Void call() throws Exception {
        ResourceAccessor resourceAccessor = new DirectoryResourceAccessor(Path.of("services/mock/src/main/resources/db"));
        var parser = ChangeLogParserFactory.getInstance()
                .getParser("liquibase-changeLog.xml", resourceAccessor);
        var changeLog = parser.parse("liquibase-changeLog.xml", new ChangeLogParameters(), resourceAccessor);

        var serializer = ChangeLogSerializerFactory.getInstance().getSerializer("liquibase-changeLog.yml");
        try (var ymlOutputstream = Files.newOutputStream(Path.of("services/mock/src/main/resources/db", "liquibase-changeLog.yml"))) {
            serializer.write(changeLog.getChangeSets(), ymlOutputstream);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        var cmd = new ConvertCommand();
        cmd.call();
    }
}
