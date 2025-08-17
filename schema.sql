-- Users
CREATE TABLE users (
  id UUID AUTO PRIMARY KEY,
  email TEXT NOT NULL,
  username TEXT NOT NULL,
  password_hash TEXT NOT NULL,
  api_key TEXT NOT NULL,
  email_verified BOOLEAN,
  is_active BOOLEAN,
  profile_data JSON,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

-- Packages
CREATE TABLE packages (
  name TEXT PRIMARY KEY,
  display_name TEXT,
  owner_id UUID NOT NULL, -- Foreign key to users(id)
  description TEXT NOT NULL,
  git_url TEXT NOT NULL,
  homepage TEXT,
  documentation TEXT,
  license TEXT,
  metadata JSON, -- Contains keywords, categories, platforms, etc.
  downloads BIGINT,
  weekly_downloads BIGINT,
  is_deprecated BOOLEAN,
  deprecation_message TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

-- Package Versions
CREATE TABLE package_versions (
  package_name TEXT NOT NULL, -- Foreign key to packages(name)
  version TEXT NOT NULL,
  git_tag TEXT NOT NULL,
  git_commit TEXT NOT NULL,
  description TEXT,
  version_metadata JSON, -- Contains dependencies, files, compatibility, etc.
  is_prerelease BOOLEAN,
  is_yanked BOOLEAN,
  yank_reason TEXT,
  published_at TIMESTAMP,
  published_by UUID, -- Foreign key to users(id)
  PRIMARY KEY (package_name, version)
);

-- Download Statistics
CREATE TABLE download_stats (
  package_name TEXT NOT NULL, -- Foreign key to packages(name)
  version TEXT,
  date TEXT NOT NULL, -- ISO date string
  downloads BIGINT,
  PRIMARY KEY (package_name, version, date)
);

-- Package Ownership
CREATE TABLE package_ownerships (
  package_name TEXT NOT NULL, -- Foreign key to packages(name)
  user_id UUID NOT NULL, -- Foreign key to users(id)
  role TEXT NOT NULL, -- 'owner', 'maintainer', 'contributor'
  granted_at TIMESTAMP,
  granted_by UUID, -- Foreign key to users(id)
  PRIMARY KEY (package_name, user_id)
);