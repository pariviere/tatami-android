package tatami.android;

import tatami.android.model.Status;
import tatami.android.request.ConversationDetails;
import android.app.Application;
import android.app.Service;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.memory.LruCacheObjectPersister;
import com.octo.android.robospice.request.SpiceRequest;

/**
 * <p>
 * Implementation of {@link SpiceService} for Tatami. This class has to be
 * referenced as {@link Service} in <i>AndroidManifest.xml</i> in order to
 * proper handle {@link SpiceRequest}
 * </p>
 * 
 * TODO find a better to handle cache lifecycle
 * 
 * @author pariviere
 */
public class AsyncRequestHandler extends SpiceService {

	@Override
	public CacheManager createCacheManager(Application application) {

		CacheManager cacheManager = new CacheManager();

		// Every object which is intented to be cached by Robospice 
		// has its own dedicated cache
		// Registration is required via cacheManager.addPersister
		LruCacheObjectPersister<Status> statusPersister = new LruCacheObjectPersister<Status>(
				Status.class, TatamiApp.statusCache);
		cacheManager.addPersister(statusPersister);

		LruCacheObjectPersister<ConversationDetails> conversationPersister = new LruCacheObjectPersister<ConversationDetails>(
				ConversationDetails.class, TatamiApp.conversationCache);
		cacheManager.addPersister(conversationPersister);

		return cacheManager;
	}

}
