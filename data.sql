-- Test users
INSERT INTO users (id, email, username, password_hash, api_key, email_verified, is_active, created_at, updated_at)
VALUES (
  '550e8400-e29b-41d4-a716-446655440000',
  'unity@example.com', 
  'unity-maintainer',
  'dummy-hash',
  'test-api-key-1',
  true,
  true,
  '2020-03-15T10:00:00Z',
  '2024-12-01T14:30:00Z'
);

INSERT INTO users (id, email, username, password_hash, api_key, email_verified, is_active, created_at, updated_at)
VALUES (
  '550e8400-e29b-41d4-a716-446655440001',
  'cjson@example.com', 
  'cjson-maintainer',
  'dummy-hash',
  'test-api-key-2',
  true,
  true,
  '2019-08-12T16:20:00Z',
  '2024-11-20T11:45:00Z'
);

-- Test packages
INSERT INTO packages (
  name, display_name, owner_id, description, git_url, homepage, documentation,
  license, metadata, downloads, weekly_downloads, is_deprecated, created_at, updated_at
) VALUES (
  'unity',
  'Unity Test Framework',
  '550e8400-e29b-41d4-a716-446655440000',
  'Lightweight unit testing framework for C',
  'https://github.com/ThrowTheSwitch/Unity',
  'https://www.throwtheswitch.org/unity',
  'https://github.com/ThrowTheSwitch/Unity/blob/master/docs/',
  'MIT',
  '{"keywords": ["testing", "unit-test", "c", "embedded"], "categories": ["testing", "development-tools"], "platforms": ["linux", "windows", "macos", "embedded"]}',
  15420,
  285,
  false,
  '2020-03-15T10:00:00Z',
  '2024-12-01T14:30:00Z'
);

INSERT INTO packages (
  name, display_name, owner_id, description, git_url, license, metadata,
  downloads, weekly_downloads, is_deprecated, created_at, updated_at
) VALUES (
  'cjson',
  'cJSON',
  '550e8400-e29b-41d4-a716-446655440001',
  'Ultralightweight JSON parser in ANSI C',
  'https://github.com/DaveGamble/cJSON',
  'MIT',
  '{"keywords": ["json", "parser", "lightweight"], "categories": ["data-processing", "serialization"], "platforms": ["linux", "windows", "macos", "embedded"]}',
  8930,
  142,
  false,
  '2019-08-12T16:20:00Z',
  '2024-11-20T11:45:00Z'
);

-- Package versions (using SELECT to get package_id from packages table)
INSERT INTO package_versions (
  package_id, version, git_tag, git_commit, description, version_metadata,
  is_prerelease, is_yanked, published_at, published_by
) VALUES (
  (SELECT id FROM packages WHERE name = 'unity'),
  '2.5.2', 'v2.5.2', 'abc123def456789',
  'Latest stable release with improved assertions',
  '{"dependencies": [], "systemRequirements": ["C compiler", "Make or CMake"]}',
  false, false, '2024-12-01T14:30:00Z',
  '550e8400-e29b-41d4-a716-446655440000'
);

INSERT INTO package_versions (
  package_id, version, git_tag, git_commit, description, version_metadata,
  is_prerelease, is_yanked, published_at, published_by
) VALUES (
  (SELECT id FROM packages WHERE name = 'unity'),
  '2.5.1', 'v2.5.1', 'def456abc123789',
  'Bug fixes and performance improvements',
  '{"dependencies": [], "systemRequirements": ["C compiler", "Make or CMake"]}',
  false, false, '2024-10-15T09:15:00Z',
  '550e8400-e29b-41d4-a716-446655440000'
);

INSERT INTO package_versions (
  package_id, version, git_tag, git_commit, description, version_metadata,
  is_prerelease, is_yanked, published_at, published_by
) VALUES (
  (SELECT id FROM packages WHERE name = 'cjson'),
  '1.7.17', 'v1.7.17', 'xyz789abc123def',
  'Security fixes and memory optimizations',
  '{"dependencies": [], "systemRequirements": ["C compiler"]}',
  false, false, '2024-11-20T11:45:00Z',
  '550e8400-e29b-41d4-a716-446655440001'
);

-- Package ownerships (using SELECT to get package_id)
INSERT INTO package_ownerships (package_id, user_id, role, granted_at, granted_by)
VALUES (
  (SELECT id FROM packages WHERE name = 'unity'),
  '550e8400-e29b-41d4-a716-446655440000',
  'owner',
  '2020-03-15T10:00:00Z',
  '550e8400-e29b-41d4-a716-446655440000'
);

INSERT INTO package_ownerships (package_id, user_id, role, granted_at, granted_by)
VALUES (
  (SELECT id FROM packages WHERE name = 'cjson'),
  '550e8400-e29b-41d4-a716-446655440001',
  'owner',
  '2019-08-12T16:20:00Z',
  '550e8400-e29b-41d4-a716-446655440001'
);