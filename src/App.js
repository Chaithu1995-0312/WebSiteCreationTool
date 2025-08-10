import React, { useState } from 'react';
import './App.css';
import axios from 'axios';
import ContentEditor from './components/ContentEditor';
import ProductionDiagramGenerator from './components/ProductionDiagramGenerator';


function App() {
  const [currentView, setCurrentView] = useState('research');
  const [topic, setTopic] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    if (!topic.trim()) {
      setError('Please enter a topic');
      return;
    }

    setLoading(true);
    setError('');
    setResult(null);

    try {
      const endpoint = currentView === 'research' 
        ? 'http://localhost:8080/api/research/query'
        : 'http://localhost:8080/api/content/generate';
        
      const response = await axios.post(endpoint, { topic: topic });
      setResult(response.data);
    } catch (err) {
      setError(err.response?.data?.error || 'Request failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="App">
      <header className="app-header">
        <h1>🔍 AI Research & Content Portal</h1>
        <p>Research topics and generate comprehensive content</p>
        
        <div className="view-switcher">
          <button 
            className={`view-button ${currentView === 'research' ? 'active' : ''}`}
            onClick={() => setCurrentView('research')}
          >
            🔍 Research Mode
          </button>
          <button 
            className={`view-button ${currentView === 'content' ? 'active' : ''}`}
            onClick={() => setCurrentView('content')}
          >
            ✍️ Content Generation
          </button>
        </div>
      </header>

      <main className="main-content">
        <div className="search-section">
          <div className="search-container">
            <input
              type="text"
              value={topic}
              onChange={(e) => setTopic(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder={
                currentView === 'research' 
                  ? "Enter a topic to research..." 
                  : "Enter a topic to generate content..."
              }
              className="search-input"
              disabled={loading}
            />
            <button 
              onClick={handleSearch} 
              disabled={loading || !topic.trim()}
              className="search-button"
            >
              {loading ? 'Processing...' : (currentView === 'research' ? 'Research' : 'Generate')}
            </button>
          </div>
        </div>

        {loading && (
          <div className="loading-section">
            <div className="loading-spinner"></div>
            <p>{currentView === 'research' ? 'Conducting research...' : 'Generating content...'}</p>
            <div className="loading-steps">
              <div className="step">🔍 Searching the web...</div>
              <div className="step">📄 Analyzing content...</div>
              <div className="step">🤖 Generating insights...</div>
              {currentView === 'content' && <div className="step">✍️ Creating article...</div>}
            </div>
          </div>
        )}

        {error && (
          <div className="error-section">
            <p>❌ {error}</p>
          </div>
        )}

        {result && currentView === 'research' && (
          <div className="results-section">
            <div className="result-card">
              <h2 className="result-title">{result.title}</h2>
              <div className="result-tabs">
                <ResultTabs result={result} />
              </div>
            </div>
          </div>
        )}

        {result && currentView === 'content' && (
          <ContentEditor content={result} onUpdate={setResult} />
        )}
      </main>
    </div>
  );
}

function ResultTabs({ result }) {
  const [activeTab, setActiveTab] = useState('overview');

  // Debug: Log the result to see what data we're getting
  console.log('ResultTabs - result data:', result);

  return (
    <div className="tabs-container">
      <div className="tabs-header">
        <button 
          className={`tab ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          📋 Overview
        </button>
        <button 
          className={`tab ${activeTab === 'detailed' ? 'active' : ''}`}
          onClick={() => setActiveTab('detailed')}
        >
          📖 Detailed
        </button>
        <button 
          className={`tab ${activeTab === 'diagrams' ? 'active' : ''}`}
          onClick={() => setActiveTab('diagrams')}
        >
          📊 Diagrams
        </button>
        <button 
          className={`tab ${activeTab === 'related' ? 'active' : ''}`}
          onClick={() => setActiveTab('related')}
        >
          🔗 Related
        </button>
        <button 
          className={`tab ${activeTab === 'sources' ? 'active' : ''}`}
          onClick={() => setActiveTab('sources')}
        >
          🔗 Sources
        </button>
      </div>

      <div className="tab-content">
        {activeTab === 'overview' && (
          <div className="overview-content">
            <div className="summary-section">
              <h3>📝 Summary</h3>
              <p className="summary-text">{result.summary || 'No summary available'}</p>
            </div>
            
            <div className="key-points-section">
              <h3>🎯 Key Points</h3>
              <ul className="key-points-list">
                {result.keyPoints && result.keyPoints.length > 0 ? (
                  result.keyPoints.map((point, index) => (
                    <li key={index} className="key-point">{point}</li>
                  ))
                ) : (
                  <li className="key-point">No key points available</li>
                )}
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'detailed' && (
          <div className="detailed-content">
            <h3>📖 Comprehensive Analysis</h3>
            <div className="comprehensive-text">
              {result.comprehensiveResponse || result.articleText || 'No detailed content available'}
            </div>
          </div>
        )}

       // Add this to your App.js in the diagrams section
 {/* Updated Diagrams Tab */}
{/* Diagrams Tab - Auto-generating DALL-E 3 */}
{activeTab === 'diagrams' && (
  <div className="diagrams-content">
    <div className="diagrams-header">
      <h3>🎨 AI-Generated Diagrams</h3>
      <p className="diagrams-intro">
        Professional diagrams for <strong>{result.topic || result.title || 'this topic'}</strong>
        automatically generated using DALL-E 3
      </p>
      <div className="auto-gen-notice">
        <span className="notice-icon">⚡</span>
        <span>Images are being generated automatically - no action needed!</span>
      </div>
    </div>

    <div className="diagram-ideas">
      {result.diagramIdeas && Array.isArray(result.diagramIdeas) && result.diagramIdeas.length > 0 ? (
        result.diagramIdeas.map((idea, index) => (
          <ProductionDiagramGenerator
            key={`${idea}-${index}`} // Better key for re-generation
            diagramIdea={idea}
            index={index}
            topic={result.topic || result.title || 'Unknown Topic'}
          />
        ))
      ) : (
        <div className="no-content">
          <p>No diagram ideas available for auto-generation</p>
        </div>
      )}
    </div>
  </div>
)}
        {activeTab === 'related' && (
          <div className="related-content">
            <h3>🔗 Related Topics</h3>
            <p className="related-intro">
              Explore these related topics to deepen your understanding:
            </p>
            <div className="related-topics">
              {result.relatedTopics && result.relatedTopics.length > 0 ? (
                result.relatedTopics.map((topic, index) => (
                  <div key={index} className="related-topic">
                    <span className="topic-bullet">•</span>
                    <span className="topic-text">{topic}</span>
                  </div>
                ))
              ) : (
                <div className="no-content">No related topics available</div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'sources' && (
          <div className="sources-content">
            <h3>🔗 Research Sources</h3>
            <div className="sources-list">
              {result.sourceUrls && result.sourceUrls.length > 0 ? (
                result.sourceUrls.map((url, index) => (
                  <div key={index} className="source-item">
                    <span className="source-number">{index + 1}</span>
                    <a 
                      href={url} 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="source-link"
                    >
                      {url}
                    </a>
                  </div>
                ))
              ) : (
                <div className="no-content">No sources available</div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;