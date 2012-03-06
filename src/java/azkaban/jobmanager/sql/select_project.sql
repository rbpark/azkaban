profiles = load '/data/databases/MEMBER2/MEMBER_PROFILE/20120305232807' USING AvroStorage();

badProfiles = filter profiles BY NOT ValidateXML(XML_CONTENT);

rmf /user/rpark/badProfiles
store badProfiles into '/user/rpark/badProfiles' USING BinaryStorage('TXN');
