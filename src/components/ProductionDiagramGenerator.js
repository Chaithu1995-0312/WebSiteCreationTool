import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const AutoGeneratingIndicator = ({ progress }) => (
  <div className="auto-generating">
    <div className="auto-gen-header">
      <span className="ai-icon">🎨</span>
      <h4>Auto-Generating DALL-E 3 Diagram...</h4>
    </div>

    <div className="progress-bar">
      <div
        className="progress-fill dalle-progress"
        style={{ width: `${progress}%` }}
      />
    </div>

    <div className="generation-steps">
      <div className={`step ${progress > 20 ? 'completed' : 'active'}`}>
        🧠 AI analyzing "{progress > 20 ? '✓' : '⏳'}"
      </div>
      <div className={`step ${progress > 60 ? 'completed' : progress > 20 ? 'active' : ''}`}>
        🎨 DALL-E 3 creating "{progress > 60 ? '✓' : progress > 20 ? '⏳' : '⏸'}"
      </div>
      <div className={`step ${progress > 85 ? 'completed' : progress > 60 ? 'active' : ''}`}>
        ✨ Finalizing "{progress > 85 ? '✓' : progress > 60 ? '⏳' : '⏸'}"
      </div>
    </div>

    <div className="auto-gen-note">
      <small>✨ Images are being generated automatically using OpenAI's DALL-E 3</small>
    </div>
  </div>
);

const DiagramDisplay = ({ diagram, onRegenerate }) => {
  if (!diagram) return null;

  return (
    <div className="diagram-display">
      <div className="display-header">
        <div className="success-indicator">
          <span className="success-icon">✅</span>
          <h4>AI-Generated Diagram</h4>
        </div>
        <button onClick={onRegenerate} className="regenerate-btn">
          🔄 Generate New Version
        </button>
      </div>

      <div className="image-display">
        {diagram.imageUrl ? (
          <div className="image-container">
            <img
              src={diagram.imageUrl}
              alt={diagram.description}
              className="dalle-generated-image"
              onLoad={() => console.log('DALL-E image loaded successfully')}
              onError={(e) => {
                console.error('DALL-E image failed to load:', diagram.imageUrl);
                e.target.style.display = 'none';
              }}
            />
          </div>
        ) : (
          <div className="image-placeholder">
            <p>✅ DALL-E 3 diagram generated successfully</p>
            <small>Provider: {diagram.provider}</small>
          </div>
        )}
      </div>

      <div className="diagram-info">
        <div className="info-badges">
          <span className="provider-badge">DALL-E 3</span>
          <span className="format-badge">PNG</span>
          <span className="resolution-badge">1024×1024</span>
        </div>
        <span className="generation-time">
          Generated at {new Date(diagram.generatedAt).toLocaleTimeString()}
        </span>
      </div>
    </div>
  );
};

const ErrorDisplay = ({ error, onRetry }) => (
  <div className="error-display">
    <div className="error-content">
      <span className="error-icon">❌</span>
      <div className="error-details">
        <h4>Generation Failed</h4>
        <p>{error}</p>
      </div>
      <button onClick={onRetry} className="retry-button">
        🔄 Try Again
      </button>
    </div>
  </div>
);

function ProductionDiagramGenerator({ diagramIdea, index, topic }) {
  const [isGenerating, setIsGenerating] = useState(true); // Start as generating
  const [generatedDiagram, setGeneratedDiagram] = useState(null);
  const [error, setError] = useState(null);
  const [progress, setProgress] = useState(0);

  const generateDiagram = useCallback(async () => {
    setIsGenerating(true);
    setError(null);
    setProgress(0);
    setGeneratedDiagram(null);

    try {
      // Enhanced progress simulation
      const progressInterval = setInterval(() => {
        setProgress(prev => {
          if (prev < 20) return prev + 4;  // Analysis phase
          if (prev < 60) return prev + 2;  // Generation phase
          if (prev < 85) return prev + 1.5; // Finalization
          return Math.min(prev + 0.5, 95);
        });
      }, 300);

      const request = {
        description: diagramIdea,
        topic: topic,
        preferredType: 'dalle',
        style: 'professional',
        format: 'png',
        width: 1024,
        height: 1024,
        colorScheme: 'professional'
      };

      console.log(`Auto-generating DALL-E 3 diagram ${index + 1}:`, request);

      const response = await axios.post('http://localhost:8080/api/diagrams/generate', request);

      clearInterval(progressInterval);
      setProgress(100);

      // Small delay to show completion
      setTimeout(() => {
        setGeneratedDiagram(response.data);
        setIsGenerating(false);
        setProgress(0);
        console.log(`Diagram ${index + 1} generated successfully:`, response.data);
      }, 500);

    } catch (err) {
      console.error(`DALL-E 3 generation failed for diagram ${index + 1}:`, err);
      setError(err.response?.data?.message || 'Failed to generate diagram with DALL-E 3');
      setIsGenerating(false);
      setProgress(0);
    }
  }, [diagramIdea, topic, index]);

  // Auto-generate on component mount
  useEffect(() => {
    console.log(`ProductionDiagramGenerator ${index + 1} mounted, auto-generating...`);
    generateDiagram();
  }, [generateDiagram]);

  const handleRegenerate = useCallback(() => {
    console.log(`Regenerating diagram ${index + 1}...`);
    generateDiagram();
  }, [generateDiagram]);

  return (
    <div className="production-diagram-generator auto-mode">
      <div className="generator-header">
        <div className="diagram-info">
          <span className="diagram-number">{index + 1}</span>
          <div className="diagram-details">
            <h3 className="diagram-title">{diagramIdea}</h3>
            <div className="auto-badge">
              <span className="auto-icon">⚡</span>
              <span>Auto-Generated</span>
            </div>
          </div>
        </div>
      </div>

      <div className="generator-content">
        {isGenerating && (
          <AutoGeneratingIndicator progress={progress} />
        )}

        {error && (
          <ErrorDisplay error={error} onRetry={handleRegenerate} />
        )}

        {generatedDiagram && !isGenerating && (
          <DiagramDisplay
            diagram={generatedDiagram}
            onRegenerate={handleRegenerate}
          />
        )}
      </div>
    </div>
  );
}

export default ProductionDiagramGenerator;