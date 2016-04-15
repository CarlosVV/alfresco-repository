package org.alfresco.repo.action.evaluator;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.evaluator.compare.ComparePropertyValueOperation;
import org.alfresco.repo.action.evaluator.compare.ContentPropertyName;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionServiceException;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Compare mime type evaluator
 * 
 * @author Roy Wetherall
 */
public class CompareMimeTypeEvaluator extends ComparePropertyValueEvaluator
{
	/**
	 * Evaluator constants
	 */
	public static final String NAME = "compare-mime-type";
    
    /**
     * 
     */
    private static final String ERRID_NOT_A_CONTENT_TYPE = "compare_mime_type_evaluator.not_a_content_type";
    private static final String ERRID_NO_PROPERTY_DEFINTION_FOUND = "compare_mime_type_evaluator.no_property_definition_found"; 
    
    /**
     * @see org.alfresco.repo.action.evaluator.ActionConditionEvaluatorAbstractBase#evaluateImpl(org.alfresco.service.cmr.action.ActionCondition, org.alfresco.service.cmr.repository.NodeRef)
     */
    public boolean evaluateImpl(ActionCondition actionCondition, NodeRef actionedUponNodeRef)
    {
        QName propertyQName = (QName)actionCondition.getParameterValue(ComparePropertyValueEvaluator.PARAM_PROPERTY);
        if (propertyQName == null)
        {
            // Default to the standard content property
            actionCondition.setParameterValue(ComparePropertyValueEvaluator.PARAM_PROPERTY, ContentModel.PROP_CONTENT);
        }
        else
        {
            // Ensure that we are dealing with a content property
            QName propertyTypeQName = null;
            PropertyDefinition propertyDefintion = this.dictionaryService.getProperty(propertyQName);
            if (propertyDefintion != null)
            {
                propertyTypeQName = propertyDefintion.getDataType().getName();
                if (DataTypeDefinition.CONTENT.equals(propertyTypeQName) == false)
                {
                    throw new ActionServiceException(ERRID_NOT_A_CONTENT_TYPE);
                }
            }
            else
            {
                throw new ActionServiceException(ERRID_NO_PROPERTY_DEFINTION_FOUND);
            }
        }
        
        // Set the operation to equals
        actionCondition.setParameterValue(ComparePropertyValueEvaluator.PARAM_OPERATION, ComparePropertyValueOperation.EQUALS.toString());
        
        // Set the content property to be MIMETYPE
        actionCondition.setParameterValue(ComparePropertyValueEvaluator.PARAM_CONTENT_PROPERTY, ContentPropertyName.MIME_TYPE.toString());

        return super.evaluateImpl(actionCondition, actionedUponNodeRef);
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) 
	{
        paramList.add(new ParameterDefinitionImpl(PARAM_PROPERTY, DataTypeDefinition.QNAME, false, getParamDisplayLabel(PARAM_PROPERTY)));
        paramList.add(new ParameterDefinitionImpl(PARAM_VALUE, DataTypeDefinition.ANY, true, getParamDisplayLabel(PARAM_VALUE), false, "ac-mimetypes"));        	    
	}
}
