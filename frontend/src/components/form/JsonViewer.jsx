import React from 'react';
import PropTypes from 'prop-types';
import { 
  Typography,
  Box
} from '@mui/material';
import { 
  RichTreeView,
  TreeItem
} from '@mui/x-tree-view';
import {
  ArrowDropDown as ArrowDropDownIcon,
  ArrowRight as ArrowRightIcon
} from '@mui/icons-material';

/**
 * Custom JSON viewer component using MUI
 * @param {Object} props Component props
 * @param {Object} props.json JSON object to display
 * @param {number} [props.collapseDepth=1] Depth to initially collapse nodes
 * @returns {JSX.Element} JSON viewer component
 */
const JsonViewer = ({ json, collapseDepth = 1 }) => {
  const renderNode = (node, key, depth = 0) => {
    if (typeof node === 'object' && node !== null) {
      const isCollapsedInitially = depth < collapseDepth;
      return (
        <TreeItem 
          key={key}
          nodeId={key}
          label={Array.isArray(node) ? `[${key}]` : key}
          defaultCollapsed={isCollapsedInitially}
        >
          {Object.entries(node).map(([k, v]) => (
            <React.Fragment key={k}>
              {renderNode(v, k, depth + 1)}
            </React.Fragment>
          ))}
        </TreeItem>
      );
    }

    return (
      <TreeItem
        key={key}
        nodeId={`${key}-value`}
        label={
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="body2" component="span" sx={{ 
              fontWeight: 'bold',
              marginRight: 1
            }}>
              {key}: 
            </Typography>
            <Typography variant="body2" component="span">
              {String(node)}
            </Typography>
          </Box>
        }
      />
    );
  };

  return (
    <RichTreeView
      defaultCollapseIcon={<ArrowDropDownIcon />}
      defaultExpandIcon={<ArrowRightIcon />}
      sx={{ 
        fontFamily: 'monospace',
        fontSize: '0.875rem'
      }}
    >
      {renderNode(json, 'root')}
    </RichTreeView>
  );
};

JsonViewer.propTypes = {
  json: PropTypes.object.isRequired,
  collapseDepth: PropTypes.number
};

export default JsonViewer;
