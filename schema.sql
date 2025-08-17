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
  owner_id UUID NOT NULL,
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
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Package Versions
CREATE TABLE package_versions (
  id UUID AUTO PRIMARY KEY,
  package_id UUID NOT NULL,
  version TEXT NOT NULL,
  git_tag TEXT NOT NULL,
  git_commit TEXT NOT NULL,
  description TEXT,
  version_metadata JSON,
  is_prerelease BOOLEAN DEFAULT FALSE,
  is_yanked BOOLEAN DEFAULT FALSE,
  yank_reason TEXT,
  published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  published_by UUID NOT NULL,
  FOREIGN KEY (package_id) REFERENCES packages(id),
  FOREIGN KEY (published_by) REFERENCES users(id),
  UNIQUE (package_id, version)
);

-- Download Statistics
CREATE TABLE download_stats (
  id UUID AUTO PRIMARY KEY,
  package_id UUID NOT NULL,
  version_id UUID,
  date TEXT NOT NULL,
  downloads BIGINT DEFAULT 0,
  FOREIGN KEY (package_id) REFERENCES packages(id),
  FOREIGN KEY (version_id) REFERENCES package_versions(id),
  UNIQUE (package_id, version_id, date)
);

-- Package Ownership
CREATE TABLE package_ownerships (
  id UUID AUTO PRIMARY KEY,
  package_id UUID NOT NULL,
  user_id UUID NOT NULL,
  role TEXT NOT NULL DEFAULT 'maintainer',
  granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  granted_by UUID,
  FOREIGN KEY (package_id) REFERENCES packages(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (granted_by) REFERENCES users(id),
  UNIQUE (package_id, user_id)
);