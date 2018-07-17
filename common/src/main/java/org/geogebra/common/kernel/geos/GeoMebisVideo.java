package org.geogebra.common.kernel.geos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.media.MebisError;
import org.geogebra.common.media.MebisURL;
import org.geogebra.common.media.MediaFormat;

/**
 * GeoElement to handle videos from Mebis.
 * 
 * @author laszlo
 *
 */
public class GeoMebisVideo extends GeoMP4Video {
	private static final String PARAM_DOC = "doc";
	private static final String DOC_RECORD = "record";
	private static final String DOC_PROVIDE_VIDEO = "provideVideo";
	private static final String DOC_EMBEDDED_OBJECT = "embeddedObject";

	private static final String PARAM_ID = "id";
	private static final String PARAM_IDENTIFIER = "identifier";

	private static final String PARAM_TYPE = "type";
	private static final String TYPE_VIDEO = "video";

	private static final String PARAM_TIME = "#t";
	private static final String PARAM_START = "start";

	/** Mebis site base URL */
	public static final String BASE_URL = "https://mediathek.mebis.bayern.de/?";
	// private String mebisId = null;

	/**
	 * 
	 * @param c
	 *            construction
	 */
	public GeoMebisVideo(Construction c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor.
	 *
	 * @param c
	 *            the construction.
	 * @param url
	 *            the URL of the video.
	 */
	public GeoMebisVideo(Construction c, String url) {
		super(c, url);
	}

	@Override
	protected void constructIds() {
		// TODO: implement if needed
	}

	@Override
	protected void createPreview() {
		// TODO: implement this
	}

	@Override
	public MediaFormat getFormat() {
		return MediaFormat.VIDEO_MEBIS;
	}

	/**
	 * Transforms possible Mebis URL to a packed, standardized one.
	 * Result contains an error code if original url is not a
	 * Mebis URL.
	 * format is:
	 * https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120
	 * 
	 * @param url
	 *            to transform.
	 * @return the packed URL with error if any.
	 */
	public static MebisURL packUrl(String url) {
		if (url == null || !url.contains(BASE_URL)) {
			return new MebisURL(null, MebisError.BASE_MISMATCH);
		}
		String substring = url.replace(BASE_URL, "");
		Map<String, String> params = extractParams(substring);
		String id = null;
		String doc = params.get(PARAM_DOC);
		boolean docValid = DOC_EMBEDDED_OBJECT.equals(doc) || DOC_PROVIDE_VIDEO.equals(doc)
				|| DOC_RECORD.equals(doc);
		if (!docValid) {
			return new MebisURL(null, MebisError.DOC);
		}

		boolean typeRequired = !DOC_RECORD.equals(doc);
		
		if (typeRequired && (!params.containsKey(PARAM_TYPE)
				|| !TYPE_VIDEO.equals(params.get(PARAM_TYPE)))) {
			return new MebisURL(null, MebisError.TYPE);
		}

		if (DOC_EMBEDDED_OBJECT.equals(doc)) {
			if (params.containsKey(PARAM_ID)) {
				id = params.get(PARAM_ID);
			}
		} else if (DOC_PROVIDE_VIDEO.equals(doc) || DOC_RECORD.equals(doc)) {
			if (params.containsKey(PARAM_IDENTIFIER)) {
				id = params.get(PARAM_IDENTIFIER);
			}
		}
		if (id == null) {
			return new MebisURL(null, MebisError.ID);
		}

		StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append(PARAM_DOC);
		sb.append("=");
		sb.append(DOC_PROVIDE_VIDEO);
		sb.append("&");
		sb.append(PARAM_IDENTIFIER);
		sb.append("=");
		sb.append(id);
		sb.append("&");
		sb.append(PARAM_TYPE);
		sb.append("=");
		sb.append(TYPE_VIDEO);
		String start = params.containsKey(PARAM_START) ? params.get(PARAM_START) : null;
		if (start != null) {
			sb.append("&");
			sb.append(PARAM_TIME);
			sb.append("=");
			sb.append(start);
		}
		return new MebisURL(sb.toString(), MebisError.NONE);
	}

	private static Map<String, String> extractParams(String url) {
		if (!url.contains("&")) {
			return null;
		}

		Map<String, String> params = new HashMap<>();
		for (String item : url.split("&")) {
			String[] p = item.split("=");
			params.put(p[0], p[1]);
		}
		return params;
	}

}