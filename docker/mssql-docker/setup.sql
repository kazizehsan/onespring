if not exists(select * from sys.databases where name = 'onespring')
    BEGIN
        CREATE DATABASE [onespring]
    END;
GO