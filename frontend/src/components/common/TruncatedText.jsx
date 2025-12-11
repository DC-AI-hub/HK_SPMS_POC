import React, { useState, useEffect, useRef } from 'react';
import { Tooltip, Box } from '@mui/material';

const TruncatedText = ({ text, maxLines = 3 }) => {
  const [isTruncated, setIsTruncated] = useState(false);
  const textRef = useRef(null);

  useEffect(() => {
    if (textRef.current) {
      const lineHeight = parseInt(getComputedStyle(textRef.current).lineHeight);
      const maxHeight = lineHeight * maxLines;
      setIsTruncated(textRef.current.scrollHeight > maxHeight);
    }
  }, [text]);

  return (
    <Tooltip title={text} disableHoverListener={!isTruncated}>
      <Box
        ref={textRef}
        sx={{
          display: '-webkit-box',
          WebkitLineClamp: maxLines,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden',
          lineHeight: 1.5
        }}
      >
        {text}
      </Box>
    </Tooltip>
  );
};

export default TruncatedText;
