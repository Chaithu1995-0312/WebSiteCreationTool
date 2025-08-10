import React, { useState } from 'react';
import axios from 'axios';

function DiagramGenerator({ diagramIdea, index, topic }) {
  const [isGenerating, setIsGenerating] = useState(false);
  const [generatedDiagram, setGeneratedDiagram] = useState(null);
  const [error, setError] = useState(null);
  const [selectedType, setSelectedType] = useState('mermaid');

  const generateDiagram = async () => {
    setIsGenerating(true);
    setError(null);

    try {
      const request = {
        description: diagramIdea,
        topic: topic,
        preferredType: selectedType,
        style: 'professional',
        format: selectedType === 'dalle' ? 'png' : 'svg',
        width: 800,
        height: 600,
        colorScheme: 'blue'
      };

      console.log('Generating diagram with request:', request);

      const response = await axios.post('http://localhost:8080/api/diagrams/generate', request);

      console.log('Diagram generated:', response.data);
      setGeneratedDiagram(response.data);

    } catch (err) {
      console.error('Diagram generation failed:', err);
      setError(err.response?.data?.message || 'Failed to generate diagram');
    } finally {
      setIsGenerating(false);
    }
  };

  const downloadDiagram = () => {
    if (!generatedDiagram) return;

    if (generatedDiagram.svgContent) {
      // Download SVG
      const blob = new Blob([generatedDiagram.svgContent], { type: 'image/svg+xml' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `diagram-${index + 1}.svg`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } else if (generatedDiagram.imageUrl) {
      // Download from URL
      const a = document.createElement('a');
      a.href = generatedDiagram.imageUrl;
      a.download = `diagram-${index + 1}.png`;
      a.target = '_blank';
      a.click();
    }
  };

  const DiagramTypeSelector = () => (
    <div className="diagram-type-selector">
      <select
        value={selectedType}
        onChange={(e) => setSelectedType(e.target.value)}
        className="type-select"
      >
        <option value="mermaid">Technical Diagram (Mermaid)</option>
        <option value="dalle">AI Visual (DALL-E)</option>
        <option value="d3">Data Visualization (D3)</option>
        <option value="lucidchart">Professional (Lucidchart)</option>
      </select>
    </div>
  );

  return (
    <div className="diagram-generator">
      <div className="diagram-idea">
        <span className="diagram-number">{index + 1}</span>
        <div className="diagram-content">
          <div className="diagram-header">
            <span className="diagram-text">{diagramIdea}</span>
            {!generatedDiagram && (
              <DiagramTypeSelector />
            )}
          </div>

          {/* Generation Controls */}
          {!generatedDiagram && !isGenerating && (
            <button
              className="diagram-action generate"
              onClick={generateDiagram}
            >
              🎨 Generate {selectedType.charAt(0).toUpperCase() + selectedType.slice(1)} Diagram
            </button>
          )}

          {/* Loading State */}
          {isGenerating && (
            <div className="generating-state">
              <div className="loading-spinner"></div>
              <span>Generating {selectedType} diagram...</span>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="error-state">
              <span className="error-message">❌ {error}</span>
              <button className="diagram-action retry" onClick={generateDiagram}>
                🔄 Try Again
              </button>
            </div>
          )}

          {/* Generated Diagram Display */}
          {generatedDiagram && (
            <div className="generated-diagram-container">
              <div className="diagram-preview">
                {generatedDiagram.svgContent ? (
                  <div
                    className="svg-container"
                    dangerouslySetInnerHTML={{ __html: generatedDiagram.svgContent }}
                  />
                ) : generatedDiagram.imageUrl ? (
                  <img
                    src={generatedDiagram.imageUrl}
                    alt={generatedDiagram.description}
                    className="generated-image"
                    onError={(e) => {
                      console.error('Image failed to load:', generatedDiagram.imageUrl);
                      e.target.style.display = 'none';
                    }}
                  />
                ) : (
                  <div className="diagram-placeholder">
                    <p>✅ Diagram generated successfully</p>
                    <small>Provider: {generatedDiagram.provider}</small>
                  </div>
                )}
              </div>

              <div className="diagram-actions">
                <button
                  className="diagram-action secondary"
                  onClick={() => setGeneratedDiagram(null)}
                >
                  🔄 Generate New
                </button>
                <button
                  className="diagram-action primary"
                  onClick={downloadDiagram}
                >
                  💾 Download
                </button>
              </div>

              <div className="diagram-metadata">
                <span className="provider-badge">{generatedDiagram.provider}</span>
                <span className="format-badge">{generatedDiagram.format}</span>
                <span className="timestamp">
                  {new Date(generatedDiagram.generatedAt).toLocaleTimeString()}
                </span>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default DiagramGenerator;