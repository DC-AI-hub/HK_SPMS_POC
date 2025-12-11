-- Update company type from BUSINESS_UNIT to BUSINESS_ENTITY
UPDATE spms_company 
SET company_type = 'BUSINESS_ENTITY'
WHERE company_type = 'BUSINESS_UNIT';
