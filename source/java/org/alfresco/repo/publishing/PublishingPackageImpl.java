
package org.alfresco.repo.publishing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.publishing.PublishingPackage;
import org.alfresco.service.cmr.publishing.PublishingPackageEntry;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Brian
 * @author Nick Smith
 * @since 4.0
 */
public class PublishingPackageImpl implements PublishingPackage
{
    private final Map<NodeRef, PublishingPackageEntry> entries;
    private final Set<NodeRef> nodesToPublish;
    private final Set<NodeRef> nodesToUnpublish;
    
    public PublishingPackageImpl(Map<NodeRef, PublishingPackageEntry> entries)
    {
        Set<NodeRef> toPublish = new HashSet<NodeRef>();
        Set<NodeRef> toUnpublish = new HashSet<NodeRef>();
        for (PublishingPackageEntry entry : entries.values())
        {
            NodeRef node = entry.getNodeRef();
            if (entry.isPublish())
            {
                toPublish.add(node);
            }
            else
            {
                toUnpublish.add(node);
            }
        }
        HashMap<NodeRef, PublishingPackageEntry> entryMap = new HashMap<NodeRef, PublishingPackageEntry>(entries);
        this.entries = Collections.unmodifiableMap(entryMap);
        this.nodesToPublish = Collections.unmodifiableSet(toPublish);
        this.nodesToUnpublish = Collections.unmodifiableSet(toUnpublish);
    }
    
    /**
    * {@inheritDoc}
     */
    public Collection<PublishingPackageEntry> getEntries()
    {
        return entries.values();
    }

    public Map<NodeRef,PublishingPackageEntry> getEntryMap()
    {
        return entries;
    }
    
    /**
     * {@inheritDoc}
     */
     public Set<NodeRef> getNodesToPublish()
     {
         return nodesToPublish;
     }

     /**
     * {@inheritDoc}
     */
     @Override
     public Set<NodeRef> getNodesToUnpublish()
     {
         return nodesToUnpublish;
     }
}
