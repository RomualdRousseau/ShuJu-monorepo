set positional-arguments

#
# RECIPES
#

# Default is this help
default: help

# Print the available recipes
help:
    @just --justfile {{justfile()}} --list

# Initializatize maven, build, and run the tests
all: initialize test build

# Initializatize maven
initialize:
    mvn initialize

# Clean
clean:
    mvn clean

# Build
build:
    mvn -DskipTests package

# Run the tests
test:
    mvn -Dtest=UnitTestSuite -Dsurefire.failIfNoSpecifiedTests=false test

# Run all tests
test-full:
    mvn -Dtest=UnitFullTestSuite -Dsurefire.failIfNoSpecifiedTests=false test

# Install in the local repository
install:
	mvn -DskipTests install

# Deploy snapshot to the maven repository
deploy-snapshot:
	mvn clean deploy -DskipTests -P snapshot

# Deploy release to the maven repository
deploy-release:
	mvn clean deploy -DskipTests -P release

# Prepape a new version
prepare-version *args='':
    mvn versions:set -DnewVersion={{args}}

# Commit to the new version
commit-version:
    mvn versions:commit

# Revert to the previous verison
revert-version:
    mvn versions:revert

# Build the documentation
build-doc:
    just --justfile any2json-documents/justfile build
    mvn -P documentation site site:stage

# Update all plugins and dependencies
update-dependencies:
    mvn -DcreateChecksum=true versions:display-dependency-updates

# Update all plugins and dependencies
update-plugins:
    mvn -DcreateChecksum=true versions:display-plugin-updates
