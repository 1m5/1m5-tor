package io.onemfive.tor.client.core.circuits.hs;

import io.onemfive.tor.client.core.TorParsingException;
import io.onemfive.tor.client.core.data.HexDigest;
import io.onemfive.tor.client.core.directory.parsing.BasicDocumentParsingResult;
import io.onemfive.tor.client.core.directory.parsing.DocumentFieldParser;
import io.onemfive.tor.client.core.directory.parsing.DocumentParser;
import io.onemfive.tor.client.core.directory.parsing.DocumentParsingHandler;
import io.onemfive.tor.client.core.directory.parsing.DocumentParsingResult;
import io.onemfive.tor.client.core.directory.parsing.DocumentParsingResultHandler;
import io.onemfive.tor.client.core.TorParsingException;
import io.onemfive.tor.client.core.data.HexDigest;

public class IntroductionPointParser implements DocumentParser<IntroductionPoint>{

	private final DocumentFieldParser fieldParser;
	
	private DocumentParsingResultHandler<IntroductionPoint> resultHandler;
	private IntroductionPoint currentIntroductionPoint;
	
	public IntroductionPointParser(DocumentFieldParser fieldParser) {
		this.fieldParser = fieldParser;
		this.fieldParser.setHandler(createParsingHandler());
	}
	
	public boolean parse(DocumentParsingResultHandler<IntroductionPoint> resultHandler) {
		this.resultHandler = resultHandler;
		try {
			fieldParser.processDocument();
			return true;
		} catch(TorParsingException e) {
			resultHandler.parsingError(e.getMessage());
			return false;
		}
	}

	public DocumentParsingResult<IntroductionPoint> parse() {
		final BasicDocumentParsingResult<IntroductionPoint> result = new BasicDocumentParsingResult<IntroductionPoint>();
		parse(result);
		return result;
	}

	private DocumentParsingHandler createParsingHandler() {
		return new DocumentParsingHandler() {
			public void parseKeywordLine() {
				processKeywordLine();
			}
			
			public void endOfDocument() {
				validateAndReportIntroductionPoint(currentIntroductionPoint);
			}
		};
	}

	private void resetIntroductionPoint(HexDigest identity) {
		validateAndReportIntroductionPoint(currentIntroductionPoint);
		currentIntroductionPoint = new IntroductionPoint(identity);
	}
	
	private void validateAndReportIntroductionPoint(IntroductionPoint introductionPoint) {
		if(introductionPoint == null) {
			return;
		}
		
		if(introductionPoint.isValidDocument()) {
			resultHandler.documentParsed(introductionPoint);
		} else {
			resultHandler.documentInvalid(introductionPoint, "Invalid introduction point");
		}
	}
	
	
	private void processKeywordLine() {
		final IntroductionPointKeyword keyword = IntroductionPointKeyword.findKeyword(fieldParser.getCurrentKeyword());
		if(!keyword.equals(IntroductionPointKeyword.UNKNOWN_KEYWORD)) {
			processKeyword(keyword);
		}
	}
	
	private void processKeyword(IntroductionPointKeyword keyword) {
		switch(keyword) {
		case INTRO_AUTHENTICATION:
			break;
			
		case INTRODUCTION_POINT:
			resetIntroductionPoint(fieldParser.parseBase32Digest());
			break;
			
		case IP_ADDRESS:
			if(currentIntroductionPoint != null) {
				currentIntroductionPoint.setAddress(fieldParser.parseAddress());
			}
			break;
			
		case ONION_KEY:
			if(currentIntroductionPoint != null) {
				currentIntroductionPoint.setOnionKey(fieldParser.parsePublicKey());
			}
			break;
			
		case ONION_PORT:
			if(currentIntroductionPoint != null) {
				currentIntroductionPoint.setOnionPort(fieldParser.parsePort());
			}
			break;
			
		case SERVICE_KEY:
			if(currentIntroductionPoint != null) {
				currentIntroductionPoint.setServiceKey(fieldParser.parsePublicKey());
			}
			break;
			
		case SERVICE_AUTHENTICATION:
			break;
			
		case UNKNOWN_KEYWORD:
			break;
		}
	}
}
