-- Add new columns
ALTER TABLE spms_user ADD COLUMN description TEXT;
ALTER TABLE spms_user ADD COLUMN user_profiles_json TEXT;

-- Migrate existing profile data to JSON format
UPDATE spms_user SET user_profiles_json = 
  json_build_object(
    'firstName', profile_first_name,
    'lastName', profile_last_name,
    'avatarUrl', profile_avatar_url,
    'bio', profile_bio
  )::TEXT
WHERE profile_first_name IS NOT NULL OR profile_last_name IS NOT NULL;

-- Drop old profile columns
ALTER TABLE spms_user DROP COLUMN profile_first_name;
ALTER TABLE spms_user DROP COLUMN profile_last_name;
ALTER TABLE spms_user DROP COLUMN profile_avatar_url;
ALTER TABLE spms_user DROP COLUMN profile_bio;
