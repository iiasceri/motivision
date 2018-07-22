psql -U postgres -d postgres -c "drop database if exists motivision"
psql -U postgres -d postgres -c "create database motivision"
psql -U postgres -d postgres -c "grant all on database motivision to postgres"
cd ..
psql --username=postgres motivision < dump.sql
cd api
mvn package && java -jar target/motivision-rest-service-1.0.0.jar