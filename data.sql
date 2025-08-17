INSERT INTO users (email, username, password_hash, api_key) VALUES
  ('admin@ckit.dev', 'admin', '$2a$10$dummy.hash.for.development', 'dev_api_key_12345');

INSERT INTO packages (name, display_name, owner_id, description, git_url, license, metadata) VALUES
  ('unity', 'Unity Test Framework', (SELECT id FROM users WHERE username = 'admin'),
   'Lightweight unit testing framework for C',
   'https://github.com/ThrowTheSwitch/Unity',
   'MIT',
   '{"keywords": ["testing", "unit-test", "c", "embedded"], "categories": ["testing", "development-tools"], "platforms": ["linux", "macos", "windows"]}');

INSERT INTO packages (name, display_name, owner_id, description, git_url, license, metadata) VALUES
  ('cjson', 'cJSON', (SELECT id FROM users WHERE username = 'admin'),
   'Ultralightweight JSON parser in ANSI C',
   'https://github.com/DaveGamble/cJSON',
   'MIT',
   '{"keywords": ["json", "parser", "lightweight"], "categories": ["data-processing", "serialization"], "platforms": ["linux", "macos", "windows"]}');

INSERT INTO package_versions (package_id, version, git_tag, git_commit, description, published_by) VALUES
  ((SELECT id FROM packages WHERE name = 'unity'), '2.5.2', 'v2.5.2', 'abc123def456789',
   'Latest stable release with improved assertions', (SELECT id FROM users WHERE username = 'admin'));

INSERT INTO package_versions (package_id, version, git_tag, git_commit, description, published_by) VALUES
  ((SELECT id FROM packages WHERE name = 'unity'), '2.5.1', 'v2.5.1', 'def456abc123789',
   'Bug fixes and performance improvements', (SELECT id FROM users WHERE username = 'admin'));

INSERT INTO package_versions (package_id, version, git_tag, git_commit, description, published_by) VALUES
  ((SELECT id FROM packages WHERE name = 'cjson'), '1.7.17', 'v1.7.17', 'xyz789abc123def',
   'Security fixes and memory optimizations', (SELECT id FROM users WHERE username = 'admin'));

-- Update package download counts (these would normally be computed from download_stats)
UPDATE packages SET downloads = 15420, weekly_downloads = 285 WHERE name = 'unity';
UPDATE packages SET downloads = 8930, weekly_downloads = 142 WHERE name = 'cjson';
