
-- Users
CREATE TABLE users (
  id UUID AUTO PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  username TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  api_key TEXT NOT NULL UNIQUE,
  email_verified BOOLEAN DEFAULT FALSE,
  is_active BOOLEAN DEFAULT TRUE,
  profile_data JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Packages
CREATE TABLE packages (
  id UUID AUTO PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  display_name TEXT,
  owner_id UUID NOT NULL REFERENCES users(id),
  description TEXT NOT NULL,
  git_url TEXT NOT NULL,
  homepage TEXT,
  documentation TEXT,
  license TEXT NOT NULL,
  metadata JSON,
  downloads BIGINT DEFAULT 0,
  weekly_downloads BIGINT DEFAULT 0,
  is_deprecated BOOLEAN DEFAULT FALSE,
  deprecation_message TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Package Versions
CREATE TABLE package_versions (
  id UUID AUTO PRIMARY KEY,
  package_id UUID NOT NULL REFERENCES packages(id),
  version TEXT NOT NULL,
  git_tag TEXT NOT NULL,
  git_commit TEXT NOT NULL,
  description TEXT,
  version_metadata JSON,
  is_prerelease BOOLEAN DEFAULT FALSE,
  is_yanked BOOLEAN DEFAULT FALSE,
  yank_reason TEXT,
  published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  published_by UUID NOT NULL REFERENCES users(id),
  UNIQUE (package_id, version)
);

-- Download Statistics
CREATE TABLE download_stats (
  id UUID AUTO PRIMARY KEY,
  package_id UUID NOT NULL REFERENCES packages(id),
  version_id UUID REFERENCES package_versions(id),
  date TEXT NOT NULL,
  downloads BIGINT DEFAULT 0,
  UNIQUE (package_id, version_id, date)
);

-- Package Ownership
CREATE TABLE package_ownerships (
  id UUID AUTO PRIMARY KEY,
  package_id UUID NOT NULL REFERENCES packages(id),
  user_id UUID NOT NULL REFERENCES users(id),
  role TEXT NOT NULL DEFAULT 'maintainer',
  granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  granted_by UUID REFERENCES users(id),
  UNIQUE (package_id, user_id)
);